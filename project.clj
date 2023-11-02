(defproject differ "0.4.0"
  :description "A library for diffing, and patching, Clojure(script) data structures"
  :url "https://github.com/Skinney/differ"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :signing {:gpg-key "robin.heggelund@icloud.com"}

  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]]

  :profiles {:1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10 {:dependencies [[org.clojure/clojure "1.10.3"]]}
             :1.11 {:dependencies [[org.clojure/clojure "1.11.1"]]}
             :cljs {:dependencies [[org.clojure/clojurescript "1.11.60"]]
                    :plugins [[lein-cljsbuild "1.1.8"]]
                    :cljsbuild {:test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}
                                :builds [{:source-paths ["src" "test"]
                                          :compiler {:output-to "target/testable.js"
                                                     :optimizations :none}}]}
                    :prep-tasks [["cljsbuild" "once"]]}
             :test {:dependencies [[org.clojure/test.check "1.1.1"]]}}

  :aliases {"all-tests" ["with-profile" "cljs:1.8:1.9:1.10:1.11" "test"]})
