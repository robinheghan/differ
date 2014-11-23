;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.diff-test
  (:require [differ.diff :as diff]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))


(let [state {:one 1
             :two {:three 2
                   :four {:five "five"
                          :six true}}
             :seven 3
             :vector [1 2 true 4]}]

  (deftest alterations
    (testing "empty coll when there are no changes"
      (is (= {} (diff/alterations {:a :a} {:a :a})))
      (is (= [] (diff/alterations [1 2] [1 2])))
      (is (= #{} (diff/alterations #{1 2} #{1 2})))
      (is (= '() (diff/alterations '(1 2) '(1 2)))))

    (testing "if types are different, returns new-state"
      (is (= [1 2] (diff/alterations {:a 2} [1 2])))
      (is (= #{2 4} (diff/alterations {"test" true} #{2 4})))
      (is (= '(1 5) (diff/alterations [1 5] '(1 5))))
      (is (= 1 (diff/alterations [1 2 3] 1))))

    (testing "returns new-state if values are not equal, but not diffable"
      (is (= 2 (diff/alterations 1 2)))
      (is (= true (diff/alterations false true)))))


  (deftest map-alterations
    (testing "alterations"
      (is (= {:one 2} (diff/alterations state (assoc state :one 2))))
      (is (= {:one 2, :seven 5} (diff/alterations state (assoc state :seven 5, :one 2)))))

    (testing "works with nesting"
      (is (= {:two {:four {:five 2}}}
             (diff/alterations state (assoc-in state [:two :four :five] 2))))
      (is (= {:one 5, :two {:four 6}}
             (diff/alterations state (-> state
                                         (assoc :one 5)
                                         (assoc-in [:two :four] 6))))))

    (testing "keys can be added"
      (is (= {:two {:four {:eight 4}}}
             (diff/alterations state (assoc-in state [:two :four :eight] 4)))))

    (testing "ignore values which are not changes or additions"
      (is (= {}
             (diff/alterations (assoc-in state [:two :four :eight] 4) state)))))

  (deftest vec-alterations
    (testing "alterations"
      (is (= [2 2] (diff/alterations [1 2 3 4] [1 2 2 4])))
      (is (= [0 5 3 1] (diff/alterations [1 2 3 4] [5 2 3 1])))
      (is (= {:vector [0 2]}
             (diff/alterations state (assoc-in state [:vector 0] 2))))
      (is (= {:vector [0 5 1 3]}
             (diff/alterations state (assoc state :vector [5 3])))))

    #_(testing "works with nesting"
      (is (= {:two {:four {:five 2}}}
             (diff/alterations state (assoc-in state [:two :four :five] 2))))
      (is (= {:one 5, :two {:four 6}}
             (diff/alterations state (-> state
                                         (assoc :one 5)
                                         (assoc-in [:two :four] 6))))))

    #_(testing "keys can be added"
      (is (= {:two {:four {:eight 4}}}
             (diff/alterations state (assoc-in state [:two :four :eight] 4)))))

    #_(testing "ignore values which are not changes or additions"
      (is (= {}
             (diff/alterations (assoc-in state [:two :four :eight] 4) state)))))

  (deftest removals
    (testing "empty coll when there are no changes"
      (is (= {} (diff/removals {:a :a} {:a :a})))
      (is (= [] (diff/removals [1 2] [1 2])))
      (is (= #{} (diff/removals #{1 2} #{1 2})))
      (is (= '() (diff/removals '(1 2) '(1 2)))))

    (testing "if types are different, returns empty state of same type"
      (is (= {} (diff/removals {:a 2} [:a 2])))
      (is (= {} (diff/removals {"test" true} #{2 4})))
      (is (= [] (diff/removals [1 5] '(1 5)))))

    (testing "return state when values are not collections"
      (is (= 1 (diff/removals 1 2)))
      (is (= true (diff/removals true false)))))

  (deftest map-removals
    (testing "removals"
      (is (= {:two 0, :seven 0, :vector 0}
             (diff/removals state {:one 1}))))

    (testing "works with nesting"
      (is (= {:two {:four {:five 0}}}
             (diff/removals state (-> state
                                      (update-in [:two :four] dissoc :five)
                                      (assoc-in [:two :four :six] false))))))))
