(ns leiningen.shegon
  (:require [shegon.server]))

(defn ^:no-project-needed shegon
  "Run a ClojureScript REPL web server."
  [project & keys]

  (shegon.server/-main))

