(defproject differ "0.3.2"
  :description "A library for diffing, and patching, Clojure(script) data structures"
  :url "https://github.com/Skinney/differ"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]]

  :profiles {:1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10 {:dependencies [[org.clojure/clojure "1.10.1"]]}
             :cljs {:dependencies [[org.clojure/clojurescript "1.10.520"]]
                    :plugins [[lein-cljsbuild "1.1.2"]]
                    :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
                                :builds [{:source-paths ["src" "test"]
                                          :compiler {:output-to "target/testable.js"
                                                     :optimizations :none}}]}
                    :prep-tasks [["cljsbuild" "once"]]}}

  :aliases {"all-tests" ["with-profile" "cljs:1.8:1.9:1.10" "test"]})
