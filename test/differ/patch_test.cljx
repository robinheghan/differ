;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.patch-test
  (:require [differ.patch :as patch]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))

(deftest template
  (testing "template test"
    (is (= {:a 1} (patch/alterations {:a 1} {:b 2})))
    (is (= {:b 2} (patch/removals {:a 1} {:b 2})))))
