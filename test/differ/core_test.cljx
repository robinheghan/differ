(ns differ.core-test
  (:require [differ.core :as core]
            #+clj [clojure.test :refer [is deftest testing]]
            #+cljs [cemerick.cljs.test :as t])
  #+cljs (:require-macros [cemerick.cljs.test :refer [is deftest testing]]))

(deftest template
  (testing "example code"
    (is (= 4 (core/simple 2)))))
