(defproject differ "0.2.2-SNAPSHOT"
  :description "A library for diffing, and patching, Clojure(script) data structures"
  :url "http://github.com/Skinney/differ"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]]

  :source-paths ["src" "target/classes"]
  :test-paths ["target/test-classes"]
  :jar-exclusions [#"\.cljx"]
  :prep-tasks [["cljx-once"]]

  :profiles {:cljx {:plugins [[com.keminglabs/cljx "0.4.0"]]
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
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0-RC5"]]}
             :cljs {:dependencies [[org.clojure/clojurescript "0.0-2665"]]
                    :plugins [[lein-cljsbuild "1.0.3"]
                              [com.cemerick/clojurescript.test "0.3.3"]]
                    :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
                                :builds [{:source-paths ["target/classes" "target/test-classes"]
                                         :compiler {:output-to "target/testable.js"
                                                    :optimizations :whitespace}}]}
                    :prep-tasks [["cljsbuild" "once"]]
                    :hooks [leiningen.cljsbuild]}}

  :aliases {"all-tests" ["with-profile" "cljs:1.6:1.7:1.8" "test"]
            "cljx-once" ["with-profile" "cljx" "cljx" "once"]
            "cljx-auto" ["with-profile" "cljx" "cljx" "auto"]})
