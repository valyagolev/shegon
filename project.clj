(defproject shegon "0.1.0-SNAPSHOT"
            :description "ClojureScript REPL able to require and reload your modules."
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [org.clojure/clojurescript "0.0-1552"]
                           [org.clojure/data.json "0.2.1"]

                           [clj-stacktrace "0.2.5"]

                           [compojure "1.1.5"]

                           [jayq "2.0.0"]]
            :plugins [[lein-ring "0.8.2"]]
            :ring {:handler shegon.server/server})

