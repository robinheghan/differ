;; Copyright © 2014-2017 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.core-test
  (:require [differ.core :as core]
            #?(:clj [clojure.test :refer [is deftest testing]]
               :cljs [cljs.test :refer-macros [is deftest testing]])))

(let [old-state {:modifyMap {:stringModify "tt"
                             :numberModify 34
                             :map {:numberModify 3
                                   :stringModify "ss"
                                   :deepMap {:a 3}
                                   :numberAdd 4
                                   :number-nil 3
                                   :nil-number nil
                                   :string-nil "sn"
                                   :nil-string nil
                                   :stringAdd "ss"
                                   :map-nil {:a 1}
                                   :map-empty {:a 2}
                                   :nil-map nil
                                   :empty-map {}
                                   :nil-emptyMap nil
                                   :nil-none nil}
                             :mapUnchange {:a "ss"}}
                 :modifyVector {:vector-nil [1 {:a "ddd"} "ss"]
                                :vectorEmpty-nil []
                                :vector-empty [1 {:a "ddd"} "ss"]
                                :nil-emptyvector nil
                                :nil-vector nil
                                :empty-vector []
                                :vectorModify [1 2 3 4
                                               {:a "tt"}
                                               {:numberUnchange 3
                                                :numberModify 3
                                                :f "s"}]}
                 :modifySet {:set-nil #{1 {:a "ddd"} "ss"}
                             :setEmpty-nil #{}
                             :set-empty #{1 {:a "ddd"} "ss"}
                             :nil-emptyset nil
                             :nil-set nil
                             :empty-set #{}
                             :setModify #{1 2 3 4
                                          {:a "tt"}
                                          {:numberUnchange 3
                                           :numberModify 3
                                           :f "s"}}}}

      new-state {:modifyMap {:stringModify "ttt"
                             :numberModify 342
                             :map {:numberModify 33
                                   :stringModify "ssa"
                                   :deepMap {:a 32}
                                   :numberRemove 45
                                   :number-nil nil
                                   :nil-number 4
                                   :string-nil nil
                                   :nil-string "ns"
                                   :stringRemove "aa"
                                   :map-nil nil
                                   :map-empty {}
                                   :nil-map {:a 1}
                                   :empty-map {:a 2}
                                   :nil-emptyMap {}
                                   :none-nil nil}
                             :mapUnchange {:a "ss"}}
                 :modifyVector {:vector-nil nil
                                :vectorEmpty-nil nil
                                :vector-empty []
                                :nil-emptyvector []
                                :nil-vector [1 {:a "ddd"} "ss"]
                                :empty-vector [1 {:a "ddd"} "ss"]
                                :vectorModify [1 4 3 5 6
                                              {:numberUnchange 3
                                               :numberModify 4
                                               :a "ss"}
                                               4]}
                 :modifySet {:set-nil nil
                             :setEmpty-nil nil
                             :set-empty #{}
                             :nil-emptyset #{}
                             :nil-set #{1 {:a "ddd"} "ss"}
                             :empty-set #{3 "dd"}
                             :setModify #{1 4 3 5 6
                                          {:numberUnchange 3
                                           :numberModify 4
                                           :a "ss"}}}}

      diff-state (core/diff old-state new-state)

      old-simple-state {:one 1
                        :two {:three 3
                        :four "test"}}
      new-simple-state {:one 2
                        :five "5"
                        :two {:four "nice"}}
      alter {:one 2
             :five "5"
             :two {:four "nice"}}
      remo {:two {:three 0}}]

  (deftest diff
    (is (= [alter remo] (core/diff old-simple-state new-simple-state)))
    (is (= [[:+ 4] []] (core/diff [1 2 3] [1 2 3 4]))))

  (deftest patch
    (is (= new-simple-state (core/patch old-simple-state [alter remo])))
    (is (= new-state (core/patch old-state diff-state)))
    (is (= [1 2 3 4] (core/patch [1 2 3] [[:+ 4] []]))))

  (deftest metadata
    (let [map-meta {:type :differ/map}
          vec-meta {:type :differ/vec}
          map-test (with-meta {:name "Robin"
                               :hobbies [:soccer]}
                     map-meta)
          vec-test (with-meta [1 2 3] vec-meta)]

      (is (= map-meta (meta (core/patch map-test [{:name "Nibor"} {:hobby 0}]))))
      (is (= vec-meta (meta (core/patch vec-test [[] [1]]))))))

  (deftest vector-nil-replacement
    (let [vector-diff-old {:words ["blah"]}
          vector-diff-new {:words nil}
          vector-diff (core/diff vector-diff-old vector-diff-new)]
      (is (= vector-diff-new (core/patch vector-diff-old vector-diff))))))
