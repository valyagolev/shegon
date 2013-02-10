(ns shegon.test.repl
  (:require [shegon.repl :as repl])
  (:use [jayq.core :only [text $ append-to]])
  (:use-macros [shegon.test.macros :only [describe expect]]))

(describe "REPL" [$el ($ "<div></div>")]
  :before (append-to $el ($ "body"))

  "can be created in the element"
    (let [repl (repl/make-repl $el)]
      (doseq [param [:input :output :prompt]]
        (expect (instance? js/CodeMirror (param repl))))

      (expect (:$element repl) $el))

  :after (.remove $el))


(describe "REPL" [$el (append-to ($ "<div></div>") ($ "body"))
                  repl (repl/make-repl $el)]

  "can be printed to"
    (do
      (repl/println! repl "Hello guys")
      (expect (repl/get-output repl) "Hello guys\n")

      (repl/println! repl "Hello classy guys" :classy)
      (expect (text ($ ".classy")) "Hello classy guys")
      )

  "can be used to set input"
    (do
      (repl/set-input repl "(+ 1 2)")
      (expect (.getValue (:input repl)) "(+ 1 2)"))

  :after (.remove $el))


