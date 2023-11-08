;; Copyright © 2014-2019 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.diff
  "Provides functions to compare two clojure datastructures and return the
  difference between them. Alterations will return the elements that differ,
  the removals will return elements that only exist in one collection."
  (:require [clojure.set :as set]))

(declare alterations removals)


(defn- map-alterations [state new-state]
  (loop [[e & es] (seq new-state)
         diff (transient {})]
    (if-not e
      (persistent! diff)
      (let [k (key e)
            old-val (get state k ::none)
            new-val (alterations old-val (get new-state k))]
        (cond (and (coll? old-val) (coll? new-val) (empty? new-val)
                   (not (record? old-val)) (not (record? new-val))
                   (= (sequential? old-val) (sequential? new-val))
                   (= (set? old-val) (set? new-val)))
              (recur es diff)

              (= old-val new-val)
              (recur es diff)

              :else
              (recur es (assoc! diff k new-val)))))))

(defn- vec-alterations [state new-state]
  (loop [idx 0
         [old-val & old-rest :as old-coll] state
         [new-val & new-rest :as new-coll] new-state
         diff (transient [])]
    (if-not (seq new-coll)
      (persistent! diff)
      (let [val-diff (alterations old-val new-val)]
        (cond (empty? old-coll)
              (recur (inc idx) old-rest new-rest (conj! (conj! diff :+) val-diff))

              (= old-val new-val)
              (recur (inc idx) old-rest new-rest diff)

              :else
              (recur (inc idx) old-rest new-rest (conj! (conj! diff idx) val-diff)))))))

(defn alterations
  "Find elements that are different in new-state, when compared to state.
  The datastructure returned will be of the same type as the first argument
  passed. Works recursively on nested datastructures."
  [state new-state]
  (cond (or (record? state) (record? new-state))
        new-state

        (and (map? state) (map? new-state))
        (map-alterations state new-state)

        (and (sequential? state) (sequential? new-state))
        (if (vector? new-state)
          (vec-alterations state new-state)
          (into (list) (reverse (vec-alterations state new-state))))

        (and (set? state) (set? new-state))
        (set/difference new-state state)

        :else
        new-state))


(defn- map-removals [state new-state]
  (let [new-keys (set (keys new-state))]
    (loop [[e & es] (seq state)
           diff (transient {})]
      (if-not (some? e)
        (persistent! diff)
        (let [k (key e)]
         (if-not (contains? new-keys k)
          (recur es (assoc! diff k 0))
          (let [old-val (get state k)
                new-val (get new-state k)
                rms (removals old-val new-val)]
            (if (and (coll? rms) (seq rms))
              (recur es (assoc! diff k rms))
              (recur es diff)))))))))

(defn- vec-removals [state new-state]
  (let [diff (- (count state) (count new-state))
        empty-state []]
    (loop [idx 0
           [old-val & old-rest :as old-coll] state
           [new-val & new-rest :as new-coll] new-state
           rem (transient (conj empty-state diff))]
      (if-not (and (seq old-coll) (seq new-coll))
        (let [base (persistent! rem)]
          (if (and (= 1 (count base))
                   (>= 0 (first base)))
            empty-state
            base))
        (let [new-rem (removals old-val new-val)]
          (if (or (and (coll? new-rem) (empty? new-rem))
                  (and (= old-val new-rem) (not (or (sequential? old-val) (map? old-val) (set? old-val)))))
            (recur (inc idx) old-rest new-rest rem)
            (recur (inc idx) old-rest new-rest (conj! (conj! rem idx) new-rem))))))))

(defn removals
  "Find elements that are in state, but not in new-state.
  The datastructure returned will be of the same type as the first argument
  passed. Works recursively on nested datastructures."
  [state new-state]
  (cond (and (coll? state) (nil? new-state))
        nil

        (not (and (coll? state) (coll? new-state)))
        state

        (and (map? state) (map? new-state))
        (map-removals state new-state)

        (and (sequential? state) (sequential? new-state))
        (if (vector? new-state)
          (vec-removals state new-state)
          (into (list) (reverse (vec-removals state new-state))))

        (and (set? state) (set? new-state))
        (set/difference state new-state)

        (record? state)
        state

        (coll? state)
        (empty state)

        :else
        state))
