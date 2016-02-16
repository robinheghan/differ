(defproject differ "0.3.1"
  :description "A library for diffing, and patching, Clojure(script) data structures"
  :url "https://gitlab.com/robin.heggelund/differ"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.7.0"]]

  :profiles {:1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :cljs {:dependencies [[org.clojure/clojurescript "1.7.228"]]
                    :plugins [[lein-cljsbuild "1.1.2"]]
                    :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
                                :builds [{:source-paths ["src" "test"]
                                          :compiler {:output-to "target/testable.js"
                                                     :optimizations :none}}]}
                    :prep-tasks [["cljsbuild" "once"]]}}

  :aliases {"all-tests" ["with-profile" "cljs:1.7:1.8" "test"]})
