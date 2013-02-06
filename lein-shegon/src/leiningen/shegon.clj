(ns leiningen.shegon
  (:require [clojure.java.io :as io]
            [leiningen.core.user]
            [ring.adapter.jetty]
            [shegon.server]
            [shegon.namespaces]))

(defn ^:no-project-needed shegon
  "Run a ClojureScript REPL web server."
  [project & keys]

  (reset! shegon.namespaces/public-output-path
    (.getAbsolutePath
      (io/file (leiningen.core.user/leiningen-home) "shegon")))
  (println "Output path:" @shegon.namespaces/public-output-path)
  (ring.adapter.jetty/run-jetty shegon.server/server {
      :port   19000
      :join?  true   ; yay blocking
    }))
