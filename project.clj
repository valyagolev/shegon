(defproject shegon "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3"]
                           [org.clojure/clojurescript "0.0-1552"]
                           [clj-stacktrace "0.2.5"]]
            :plugins [[lein-cljsbuild "0.3.0"]]
            :cljsbuild {:builds [{:source-paths ["src-cljs/"]
                        :compiler {
                            :output-to "out/shegon/shegon.js"
                            ; :optimizations :simple
                            :pretty-print true
                            ; :incremental true
                            }}]}
            :main shegon.server)

