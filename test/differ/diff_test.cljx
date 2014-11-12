;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.diff-test
  (:require [differ.diff :as diff]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))

(deftest template
  (testing "template test"
    (is (= {:a 1} (diff/alterations {:a 1} {:b 2})))
    (is (= {:b 2} (diff/removals {:a 1} {:b 2})))))
