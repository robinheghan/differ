;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.diff)

(declare alterations removals)


(defn- map-alterations [state new-state]
  (loop [ks (keys new-state)
         diff {}]
    (if-let [k (first ks)]
      (let [old-val (get state k)
            new-val (alterations old-val (get new-state k))]
        (cond (and (map? new-val) (empty? new-val))
              (recur (rest ks) diff)

              (= old-val new-val)
              (recur (rest ks) diff)

              :else
              (recur (rest ks) (assoc diff k new-val))))
      diff)))

(defn alterations
  "Returns a diff of alterations from a to b"
  [state new-state]
  (if-not (= (type state) (type new-state))
    new-state
    (cond (map? state) (map-alterations state new-state)
          :else new-state)))


(defn removals
  "Returns a diff of removals from a to b"
  [state new-state]
  new-state)
