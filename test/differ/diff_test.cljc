;; Copyright © 2014-2017 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.diff-test
  (:require [differ.diff :as diff]
            #?(:clj [clojure.test :refer [is deftest testing]]
               :cljs [cljs.test :refer-macros [is deftest testing]])))


(let [state {:one 1
             :two {:three 2
                   :four {:five "five"
                          :six true}}
             :seven 3
             :vector [1 2 true 4]
             :list '(4 "by" 2)
             :set #{:b}}]

  (deftest alterations
    (testing "empty coll when there are no changes"
      (is (= {} (diff/alterations {:a :a} {:a :a})))
      (is (= [] (diff/alterations [1 2] [1 2])))
      (is (= #{} (diff/alterations #{1 2} #{1 2})))
      (is (= '() (diff/alterations '(1 2) '(1 2)))))

    (testing "if types are different, returns new-state"
      (is (= [1 2] (diff/alterations {:a 2} [1 2])))
      (is (= #{2 4} (diff/alterations {"test" true} #{2 4})))
      (is (= 1 (diff/alterations [1 2 3] 1))))

    (testing "sequential types are treated equal"
      (is (= '(1 2) (diff/alterations [1 1 1] '(1 2 1))))
      (is (= [1 2] (diff/alterations '(1 1 1) [1 2 1]))))

    (testing "returns new-state if values are not equal, but not diffable"
      (is (= 2 (diff/alterations 1 2)))
      (is (= true (diff/alterations false true)))
      (is (= "first" (diff/alterations "who's on" "first")))))


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
             (diff/alterations (assoc-in state [:two :four :eight] 4) state))))

    (testing "nil has no special treatment"
      (is (= {:a 2, :b "x", :d nil, :e 2}
             (diff/alterations {:a 1 :b 2 :c nil :d 1 :e nil}
                               {:a 2 :b "x" :c nil :d nil :e 2})))
      (is (= {:a 2 :b []}
             (diff/alterations {:a 1 :b nil}
                               {:a 2 :b []})))))

  (deftest vec-alterations
    (testing "alterations"
      (is (= [2 2] (diff/alterations [1 2 3 4] [1 2 2 4])))
      (is (= [0 5 3 1] (diff/alterations [1 2 3 4] [5 2 3 1])))
      (is (= {:vector [0 2]}
             (diff/alterations state (assoc-in state [:vector 0] 2))))
      (is (= {:vector [0 5 1 3]}
             (diff/alterations state (assoc state :vector [5 3])))))

    (testing "works with nesting"
      (is (= [1 [:+ 5]] (diff/alterations [1 []] [1 [5]])))
      (is (= [2 {:a 5}] (diff/alterations [1 2 {:a 4, :b 10}]
                                          [1 2 {:a 5, :b 10}])))
      (is (= [] (diff/alterations [5 [1 2]] [5 [1 2]]))))

    (testing "values can be added"
      (is (= [:+ 1] (diff/alterations [] [1])))
      (is (= [:+ 3 :+ 5] (diff/alterations [1] [1 3 5])))
      (is (= [1 2 :+ 2] (diff/alterations [1 1] [1 2 2])))))

  (deftest list-alterations
    (testing "alterations"
      (is (= '(2 2) (diff/alterations '(1 2 3 4) '(1 2 2 4))))
      (is (= '(0 5 3 1) (diff/alterations '(1 2 3 4) '(5 2 3 1))))
      (is (= {:list '(1 "x")}
             (diff/alterations state (assoc state :list '(4 "x" 2)))))
      (is (= {:list '(0 3 2 4)}
             (diff/alterations state (assoc state :list '(3 "by" 4))))))

    (testing "works with nesting"
      (is (= '(1 [:+ 5]) (diff/alterations '(1 []) '(1 [5]))))
      (is (= '(2 {:a 5}) (diff/alterations '(1 2 {:a 4, :b 10})
                                           '(1 2 {:a 5, :b 10}))))
      (is (= '() (diff/alterations '(5 [1 2]) '(5 [1 2])))))

    (testing "values can be added"
      (is (= '(:+ 1) (diff/alterations '() '(1))))
      (is (= '(:+ 3 :+ 5) (diff/alterations '(1) '(1 3 5))))
      (is (= '(1 2 :+ 2) (diff/alterations '(1 1) '(1 2 2))))))

  (deftest set-alterations
    (testing "Values can only be added, and there is no nesting"
      (is (= #{:a} (diff/alterations #{:c :d} #{:a :c :d})))
      (is (= {:set #{:a}}
             (diff/alterations state (assoc state :set #{:a :b}))))))


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
      (is (= {:two 0, :seven 0, :vector 0, :list 0, :set 0}
             (diff/removals state {:one 1}))))

    (testing "works with nesting"
      (is (= {:two {:four {:five 0}}}
             (diff/removals state (-> state
                                      (update-in [:two :four] dissoc :five)
                                      (assoc-in [:two :four :six] false)))))))

  (deftest vec-removals
    (testing "removals"
      (is (= [] (diff/removals [1 2 3] [3 2 1])))
      (is (= [] (diff/removals [1 2 3] [4 3 2 1])))
      (is (= [2] (diff/removals [1 2 3] [1]))))

    (testing "works with nesting"
      (is (= [1 1 [1]] (diff/removals [1 [3 4 5] 6] [1 [3 5]])))
      (is (= [0 1 {:a 0}] (diff/removals [1 {:a 2} 3] [1 {} 3])))))

  (deftest list-removals
    (testing "removals"
      (is (= '() (diff/removals '(1 2 3) '(3 2 1))))
      (is (= '() (diff/removals '(1 2 3) '(4 3 2 1))))
      (is (= '(2) (diff/removals '(1 2 3) '(1))))
      (is (= '(2) (diff/removals [1 2 3] '(1)))))

    (testing "works with nesting"
      (is (= '(1 1 (1)) (diff/removals '(1 (3 4 5) 6) '(1 (3 5)))))
      (is (= '(0 1 {:a 0}) (diff/removals '(1 {:a 2} 3) '(1 {} 3))))
      (is (= '(0 1 {:a 0}) (diff/removals [1 {:a 2} 3] '(1 {} 3))))))

  (deftest set-removals
    (testing "can only remove elements, does not support nesting"
      (is (= #{true} (diff/removals #{1 true "game"} #{1 "game"})))
      (is (= {:set #{:b}}
             (diff/removals state (assoc state :set #{})))))))
