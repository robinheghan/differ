;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.patch
  "Use the functions in this namespace to apply diffs, created by functions
in the differ.diff namespace, to similar datastructures.")

(declare alterations removals)


(defn- map-alterations [state diff]
  (loop [[k & ks] (keys diff)
         result (transient state)]
    (if-not k
      (persistent! result)
      (let [old-val (get result k)
            diff-val (get diff k)]
        (recur ks (assoc! result k (alterations old-val diff-val)))))))

(defn- vec-alterations [state diff]
  (loop [idx 0
         [old-val & old-rest :as old-coll] state
         [diff-idx diff-val & diff-rest :as diff-coll] diff
         result (transient (empty diff))]
    (let [old-empty? (empty? old-coll)
          diff-empty? (empty? diff-coll)]
      (cond (and old-empty? diff-empty?)
            (persistent! result)

            diff-empty?
            (recur (inc idx) old-rest diff-rest (conj! result old-val))

            (or (= idx diff-idx) old-empty?)
            (recur (inc idx) old-rest diff-rest (conj! result (alterations old-val diff-val)))

            :else
            (recur (inc idx) old-rest diff-coll (conj! result old-val))))))

(defn alterations
  "Returns a new datastructure, containing the changes in the provided diff."
  [state diff]
  (cond (not= (type state) (type diff))
        diff

        (map? diff)
        (map-alterations state diff)

        (vector? diff)
        (vec-alterations state diff)

        :else
        diff))


(defn- map-removals [state diff]
  (loop [[k & ks] (keys diff)
         result (transient state)]
    (if-not k
      (persistent! result)
      (let [old-val (get result k)
            diff-val (get diff k)]
        (if (map? diff-val)
          (recur ks (assoc! result k (removals old-val diff-val)))
          (recur ks (dissoc! result k)))))))

(defn removals
  "Returns a new datastructure, not containing the elements in the
  provided diff."
  [state diff]
  (cond (map? diff)
        (map-removals state diff)

        :else
        state))
