(ns shegon.test.repl
  (:require [shegon.repl :as repl])
  (:use [jayq.core :only [text $ append-to]])
  (:use-macros [shegon.test.macros :only [describe expect]]))


(describe "REPL"
  :let $el (append-to ($ "<div></div>") ($ "body"))
  :let repl (repl/make-repl $el)

  :it "can be created in the element"
    (doseq [param [:input :output :prompt]]
      (expect (instance? js/CodeMirror (param repl))))

    (expect (:$element repl) $el)

  :it "can be printed to"
    (repl/println repl "Hello guys")
    (expect (repl/get-output repl) "Hello guys\n")

  :it "can be printed to with class"
    (repl/println repl "Hello classy guys" :classy)
    (expect (text ($ ".classy")) "Hello classy guys")

  :it "can be used to set input"
    (repl/set-input repl "(+ 1 2)")
    (expect (.getValue (:input repl)) "(+ 1 2)")

  :xit "can eval-print"
    (xdo
      (repl/eval-print repl "(+ 1 2)")
      (except (repl/get-output repl) 3))

  :after (.remove $el))
