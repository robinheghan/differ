;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.patch-test
  (:require [differ.patch :as patch]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))

(let [state {:one 1
             :two {:three 2
                   :four {:five "five"
                          :six true}}
             :seven 3
             :vector [1 {:some-more [3 4 true]}]}]

  (deftest alterations
    (testing "maps"
      (is (= (assoc state :one 2)
             (patch/alterations state {:one 2})))
      (is (= (-> state
                 (assoc :seven 7)
                 (assoc-in [:two :three] {:booya "boom"})))
          (patch/alterations state {:seven 7, :two {:three {:booya "boom"}}}))
      (is (= (assoc state :eight [{}])
             (patch/alterations state {:eight [{}]})))))

  (deftest removals
    (testing "maps"
      (is (= (dissoc state :one)
             (patch/removals state {:one 0})))
      (is (= (-> state
                 (dissoc :one)
                 (update-in [:two] dissoc :four))
             (patch/removals state {:one 0, :two {:four 0}}))))))
