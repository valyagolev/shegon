(ns shegon.test.repl
  (:require [shegon.repl :as repl])
  (:use [jayq.core :only [text $ append-to]]
        [shegon.test.utils :only [deferred-to-either]])
  (:use-macros [shegon.test.macros :only [async-test describe expect]]))


(defn last-line [output]
  (last (.split (.trim output) "\n")))


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

  :it "can eval-print"
    (async-test 200
      [_ (repl/eval-print repl "(+ 1 2)")]
      (expect (repl/get-output repl) "shegon.user=>  (+ 1 2)\n3\n"))

  :it "can read-eval-print"
    (repl/set-input repl "(+ 3 4)")
    (async-test 200
      [_ (repl/read-eval-print repl)]
      (expect (repl/get-output repl) "shegon.user=>  (+ 3 4)\n7\n")
      (expect (repl/get-input repl) ""))

  :it "can read-eval-print async stuff"
    (repl/set-input repl "(let [d (jayq.core/$deferred)]
                            (js/setTimeout #(jayq.core/resolve d 123) 100) d)")
    (async-test 200
      [_ (repl/read-eval-print repl)]
      (expect (last-line (repl/get-output repl)) "123")
      (expect (repl/get-input repl) ""))

  :it "can read-eval-print erroneous async stuff"
    (repl/set-input repl "(let [d (jayq.core/$deferred)]
                            (js/setTimeout #(jayq.core/reject d 666) 100) d)")
    (async-test 200
      [_ (deferred-to-either (repl/read-eval-print repl))]

      (expect (last-line (repl/get-output repl)) "Error: 666")
      (expect (repl/get-input repl) ""))


  :after (.remove $el)
  )
