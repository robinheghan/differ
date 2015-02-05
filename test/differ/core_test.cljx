;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.core-test
  (:require [differ.core :as core]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))

(let [old-state {:modifyMap {:stringModify "tt"
                             :numberModify 34
                             :map {:numberModify 3
                                   :stringModify "ss"
                                   :deepMap{:a 3}
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
                                   :nil-disappear nil}
                             :mapUnchange {:a "ss"}}
                 :modifyList {:list-nil [1 {:a "ddd"} "ss"]
                              :listEmpty-nil []
                              :list-empty [1 {:a "ddd"} "ss"]
                              :nil-emptyList nil
                              :nil-list nil
                              :empty-list []
                              :listModify [1 2 3 4
                                           {:a "tt"}
                                           {:numberUnchange 3
                                            :numberModify 3
                                            :f "s"}]}}

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
                                   :disappear-nil nil}
                             :mapUnchange {:a "ss"}}
                 :modifyList {:list-nil nil
                              :listEmpty-nil nil
                              :list-empty []
                              :nil-emptyList []
                              :nil-list [1 {:a "ddd"} "ss"]
                              :empty-list [1 {:a "ddd"} "ss"]
                              :listModify [1 4 3 5 6
                                           {:numberUnchange 3
                                            :numberModify 4
                                            :a "ss"}
                                            4]}}

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
    (is (= [alter remo] (core/diff old-simple-state new-simple-state))))

  (deftest patch
    (is (= new-simple-state (core/patch old-simple-state [alter remo])))
    (is (= new-state (core/patch old-state diff-state)))))
