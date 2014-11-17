(defproject differ "0.1.0"
  :description "A library for diffing, and patching, Clojure(script) data structures"
  :url "http://github.com/Skinney/differ"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]]

  :profiles {:dev {:plugins [[com.keminglabs/cljx "0.4.0"]]
                   :jar-exclusions [#"\.cljx"]
                   :hooks [cljx.hooks]
                   :cljx {:builds [{:source-paths ["src"]
                                    :output-path "target/classes"
                                    :rules :clj}
                                   {:source-paths ["src"]
                                    :output-path "target/classes"
                                    :rules :cljs}
                                   {:source-paths ["test"]
                                    :output-path "target/test-classes"
                                    :rules :clj}
                                   {:source-paths ["test"]
                                    :output-path "target/test-classes"
                                    :rules :cljs}]}}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0-alpha4"]]}
             :cljs {:dependencies [[org.clojure/clojurescript "0.0-2371"]]
                    :plugins [[lein-cljsbuild "1.0.3"]
                              [com.cemerick/clojurescript.test "0.3.1"]]
                    :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
                                :builds [{:source-paths ["target/classes" "target/test-classes"]
                                         :compiler {:output-to "target/testable.js"
                                                    :optimizations :whitespace}}]}
                    :hooks [cljx.hooks
                            leiningen.cljsbuild]}}

  :source-paths ["src" "target/classes"]
  :test-paths ["target/test-classes"]
  :aliases {"all-tests" ["with-profile" "cljs:1.6:1.7" "test"]})
