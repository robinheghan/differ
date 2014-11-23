;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.diff
  "Provides functions to compare two clojure datastructures and return the
difference between them. Alterations will return the elements that differ,
the removals will return elements that only exist in one collection.")

(declare alterations removals)


(defn- map-alterations [state new-state]
  (loop [ks (keys new-state)
         diff (transient {})]
    (if-let [k (first ks)]
      (let [old-val (get state k)
            new-val (alterations old-val (get new-state k))]
        (cond (and (coll? new-val) (empty? new-val))
              (recur (rest ks) diff)

              (= old-val new-val)
              (recur (rest ks) diff)

              :else
              (recur (rest ks) (assoc! diff k new-val))))
      (persistent! diff))))

(defn- vec-alterations [state new-state]
  (loop [idx 0
         [old-val & old-rest :as old-coll] state
         [new-val & new-rest :as new-coll] new-state
         diff (transient (empty new-state))]
    (cond (empty? new-coll)
          (persistent! diff)

          (empty? old-coll)
          (recur (inc idx) old-rest new-rest (conj! (conj! diff :+) new-val))

          (= old-val new-val)
          (recur (inc idx) old-rest new-rest diff)

          :else
          (recur (inc idx) old-rest new-rest (conj! (conj! diff idx) new-val)))))

(defn alterations
  "Find elements that are different in new-state, when compared to state.
  The datastructure returned will be of the same type as the first argument
  passed. Works recursively on nested datastructures."
  [state new-state]
  (cond (not= (type state) (type new-state))
        new-state

        (map? state)
        (map-alterations state new-state)

        (vector? state)
        (vec-alterations state new-state)

        (and (coll? state) (coll? new-state) (= state new-state))
        (empty state)

        :else
        new-state))


(defn- map-removals [state new-state]
  (let [new-keys (set (keys new-state))]
    (loop [ks (keys state)
           diff (transient {})]
      (if-let [k (first ks)]
        (if (get new-keys k)
          (let [old-val (get state k)
                new-val (get new-state k)
                rms (removals old-val new-val)]
            (if (and (coll? rms) (seq rms))
              (recur (rest ks) (assoc! diff k rms))
              (recur (rest ks) diff)))
          (recur (rest ks) (assoc! diff k 0)))
        (persistent! diff)))))

(defn removals
  "Find elements that are in state, but not in new-state.
  The datastructure returned will be of the same type as the first argument
  passed. Works recursively on nested datastructures."
  [state new-state]
  (cond (not (and (coll? state) (coll? new-state)))
        state

        (not= (type state) (type new-state))
        (empty state)

        (map? state)
        (map-removals state new-state)

        :else
        (empty state)))
