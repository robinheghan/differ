;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.patch)

(declare alterations removals)

(defn- map-alterations [state diff]
  (loop [ks (keys diff)
         result state]
    (if-let [k (first ks)]
      (let [old-val (get result k)
            diff-val (get diff k)]
        (if (and (coll? old-val) (coll? diff-val))
          (recur (rest ks) (assoc result k (alterations old-val diff-val)))
          (recur (rest ks) (assoc result k diff-val))))
      result)))

(defn alterations
  "Returns the result of applying alterations to state"
  [state diff]
  (cond (map? diff)
        (map-alterations state diff)

        :else
        state))


(defn- map-removals [state diff]
  (loop [ks (keys diff)
         result state]
    (if-let [k (first ks)]
      (let [old-val (get result k)
            diff-val (get diff k)]
        (if (coll? diff-val)
          (recur (rest ks) (assoc result k (removals old-val diff-val)))
          (recur (rest ks) (dissoc result k))))
      result)))

(defn removals
  "Returns the result of applying removals to state"
  [state diff]
  (cond (map? diff)
        (map-removals state diff)

        :else
        state))
