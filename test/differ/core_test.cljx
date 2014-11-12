;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.core-test
  (:require [differ.core :as core]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))

(deftest template
  (testing "template test"
    (is (= [{:a 1} {:b 2}] (core/diff {:a 1} {:b 2})))
    (is (= {:a 1} (core/patch {:a 1} [1 2])))))
