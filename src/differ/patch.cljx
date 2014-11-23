;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.patch
  "Use the functions in this namespace to apply diffs, created by functions
in the differ.diff namespace, to similar datastructures.")

(declare alterations removals)

(defn- map-alterations [state diff]
  (loop [ks (keys diff)
         result (transient state)]
    (if-let [k (first ks)]
      (let [old-val (get result k)
            diff-val (get diff k)]
        (if (and (map? old-val) (map? diff-val))
          (recur (rest ks) (assoc! result k (alterations old-val diff-val)))
          (recur (rest ks) (assoc! result k diff-val))))
      (persistent! result))))

(defn alterations
  "Returns a new datastructure, containing the changes in the provided diff."
  [state diff]
  (cond (map? diff)
        (map-alterations state diff)

        :else
        state))


(defn- map-removals [state diff]
  (loop [ks (keys diff)
         result (transient state)]
    (if-let [k (first ks)]
      (let [old-val (get result k)
            diff-val (get diff k)]
        (if (map? diff-val)
          (recur (rest ks) (assoc! result k (removals old-val diff-val)))
          (recur (rest ks) (dissoc! result k))))
      (persistent! result))))

(defn removals
  "Returns a new datastructure, not containing the elements in the
  provided diff."
  [state diff]
  (cond (map? diff)
        (map-removals state diff)

        :else
        state))
