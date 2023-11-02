(ns differ.gen-test
  (:require [differ.core :as core]
            clojure.pprint
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def ^:private
  key-gen
  (gen/elements [0 1 :a :b "c" "d" true false nil]))

(def ^:private
  simple-type-printable-equatable
  "Like gen/simple-type-printable, but only generates objects that
  can be equal to other objects (e.g., not a NaN) and protects against
  `0` and `0.0` and `-0.0` being in the same map."
  (gen/one-of
   [gen/small-integer #?(:clj gen/size-bounded-bigint :cljs gen/large-integer)
    gen/ratio gen/boolean gen/keyword gen/keyword-ns gen/symbol gen/symbol-ns gen/uuid
    (gen/double* {:NaN? false, :infinite? false, :min 0.00000001, :max 9007199254740991})
    (gen/double* {:NaN? false, :infinite? false, :max -0.00000001, :min -9007199254740991})
    gen/char-ascii
    gen/string-ascii]))

(def ^:private
  diffable-value (gen/recursive-gen gen/container-type simple-type-printable-equatable))

(def ^:private
  coll-gen
  (gen/one-of [(gen/tuple (gen/map key-gen diffable-value) (gen/map key-gen diffable-value))
               (gen/tuple (gen/vector diffable-value)      (gen/vector diffable-value))
               (gen/tuple (gen/set diffable-value)         (gen/set diffable-value))
               (gen/tuple (gen/list diffable-value)        (gen/list diffable-value))]))

;; Generative testing: round-trip through diff-patch
(defspec diff-patch-list
  100
  (prop/for-all [[old new] coll-gen]
                (let [diff (core/diff old new)]
                  (= new (core/patch old diff)))))

;; Examples that have failed before
(deftest round-trip
  (doseq [[old new]
          [[{} {false 0}]
           [{0 []} {0 #{}}]
           [{:a []} {:a {}}]
           ['(#{0}) '(#{})]
           [{false [[]]} {false 0}]
           [[{{} 0}] [{}]]
           [[0 0 0 {0 0}] [0 0 0 {}]]
           ['([[1N]]) '([[]])]
           [(list (list (list 1N)))
            (list (list (list ) #{} (list false 0.4788818359375)) [true] #{#{:+?/N92a}} \" "D" #{:Bb. -41627433298775N false} {})]
           [(list 0 0 0 0 0 0 0 0 [0 0 0 0 0 0 0 0 0 0 0 0 0 [1 1 2]])
            (list 0 0 0 0 0 0 0 0 [0 0 0 0 0 0 0 0 0 0 0 0 0 []])]]]
    (testing (pr-str 'round-trip old '-> new)
      (is (= new (core/patch old (core/diff old new)))
          (pr-str 'round-trip 'failed 'given 'diff (core/diff old new))))))

(defn- describe
  [old new]
  (let [diff (delay (core/diff old new))
        patched (delay (core/patch old @diff))
        success? (delay (= new @patched))]
    (println "----- original (old) -----")
    (clojure.pprint/pprint old)
    (println "----- diff [alterations removals]  -----")
    (clojure.pprint/pprint @diff)
    (println "----- expected (new) -----")
    (clojure.pprint/pprint new)
    (println "----- patched -----")
    (clojure.pprint/pprint @patched)
    (println "-----" (if @success? "success" "FAILED") "-----")
    (if @success? :success :failed)))

(defn- bi-describe
  [old new]
  (println "===================== old -> new ========================")
  (describe old new)
  (println "===================== new -> old ========================")
  (describe new old))

(comment
  (bi-describe {} {false 0})
  (bi-describe {0 []} {0 #{}})
  (bi-describe {:a []} {:a {}})
  (bi-describe '(#{0}) '(#{}))
  (bi-describe {false [[]]} {false 0})
  (bi-describe [0 0 0 {0 0}] [0 0 0 {}])
  (bi-describe {"c" {}} {"c" {{Double/NaN 0} 0}})
  (bi-describe '([[1N]]) '([[]]))
  (bi-describe (list (list (list 1N))) (list (list (list ) #{} (list false 0.4788818359375)) [true] #{#{:+?/N92a}} \" "D" #{:Bb. -41627433298775N false} {}))
  (bi-describe (list 0 0 0 0 0 0 0 0 [0 0 0 0 0 0 0 0 0 0 0 0 0 [1 1 2]])
               (list 0 0 0 0 0 0 0 0 [0 0 0 0 0 0 0 0 0 0 0 0 0 []]))
  :-)
