(defproject shegon "0.1.1"
            :description "ClojureScript REPL able to require and reload your modules."
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [org.clojure/clojurescript "0.0-1552"]
                           [org.clojure/data.json "0.2.1"]
                           [leiningen-core "2.0.0"]

                           [ring "1.1.8"]
                           [compojure "1.1.5"]

                           [clj-stacktrace "0.2.5"]
                           [ring-anti-forgery "0.2.1"]

                           [jayq "2.0.0"]]
            :plugins [[lein-ring "0.8.2"]]
            :main shegon.server)

