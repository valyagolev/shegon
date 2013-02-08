(ns leiningen.shegon
  (:require [shegon.server])
  (:use [leiningen.core.eval :only [eval-in-project]]))

(defn ^:no-project-needed shegon
  "Run a ClojureScript REPL web server."
  [project & keys]

  (if project
    (eval-in-project (update-in project [:dependencies]
                                   conj ['shegon "0.1.1"])
      (.join (shegon.server/run-if-not-running)))

    (.join (shegon.server/run-if-not-running))))
