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
             :vector [1 2 {:a 3, :some-more [3 4 true]}]
             :list [1 2 {:a 3, :some-more [3 4 true]}]
             :set #{1 true "false"}}]

  (deftest alterations
    (testing "overwrite old value when types do not match, or aren't patchable"
      (is (= 5 (patch/alterations 3 5)))
      (is (= true (patch/alterations 1 true)))
      (is (= {:a 1} (patch/alterations {:a {:b 2}} {:a 1})))
      (is (= {:c 3, :a [2 3]}
             (patch/alterations {:c 3, :a {:b 2}} {:a [2 3]}))))

    (testing "maps"
      (is (= (assoc state :one 2)
             (patch/alterations state {:one 2})))
      (is (= (-> state
                 (assoc :seven 7)
                 (assoc-in [:two :three] {:booya "boom"}))
             (patch/alterations state {:seven 7, :two {:three {:booya "boom"}}})))
      (is (= (assoc state :eight [{}])
             (patch/alterations state {:eight [{}]}))))

    (testing "vectors"
      (is (= [1 3 3 5 5] (patch/alterations [1 2 3 4 5] [1 3 3 5])))
      (is (= [5] (patch/alterations [] [:+ 5])))
      (is (= [1 2 5] (patch/alterations [2 2] [0 1 :+ 5])))
      (is (= (assoc state :vector [2 2 {:a 3, :some-more [3 5 true]}])
             (patch/alterations state {:vector [0 2 2 {:some-more [1 5]}]}))))

    (testing "lists"
      (is (= '(1 3 3 5 5) (patch/alterations '(1 2 3 4 5) '(1 3 3 5))))
      (is (= '(5) (patch/alterations '() '(:+ 5))))
      (is (= '(1 2 5) (patch/alterations '(2 2) '(0 1 :+ 5))))
      (is (= (assoc state :list '(2 2 {:a 3, :some-more [3 5 true]}))
             (patch/alterations state {:list '(0 2 2 {:some-more [1 5]})}))))

    (testing "lists and vectors"
      (is (= [1 3 3 5 5] (patch/alterations '(1 2 3 4 5) [1 3 3 5])))
      (is (= '(5) (patch/alterations [] '(:+ 5)))))

    (testing "sets"
      (is (= #{:a 4 "third"} (patch/alterations #{4 :a} #{"third"})))
      (is (= (assoc state :set #{1 true "false" 2})
             (patch/alterations state {:set #{2}})))))

  (deftest removals
    (testing "maps"
      (is (= (dissoc state :one)
             (patch/removals state {:one 0})))
      (is (= (-> state
                 (dissoc :one)
                 (update-in [:two] dissoc :four))
             (patch/removals state {:one 0, :two {:four 0}}))))

    (testing "vectors"
      (is (= (assoc state :vector [1 2])
             (patch/removals state {:vector [1]})))
      (is (= (assoc state :vector [1 2 {:some-more [3]}])
             (patch/removals state {:vector [0 2 {:a 0, :some-more [2]}]}))))

    (testing "lists"
      (is (= (assoc state :list '(1 2))
             (patch/removals state {:list '(1)})))
      (is (= (assoc state :list '(1 2 {:some-more [3]}))
             (patch/removals state {:list '(0 2 {:a 0, :some-more [2]})}))))

    (testing "vectors and lists"
      (is (= '(1) (patch/removals [1 2 3] '(2))))
      (is (= [{}] (patch/removals '({:a 2} 2) [1 0 {:a 0}]))))

    (testing "sets"
      (is (= #{1} (patch/removals #{1 false} #{false})))
      (is (= (assoc state :set #{1})
             (patch/removals state {:set #{"false" true}}))))))
