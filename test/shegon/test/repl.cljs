(ns shegon.test.repl
  (:require [shegon.repl])
  (:use-macros [shegon.test.macros :only [describe expect]]))

(describe "REPL again" [$el (js/$ "<div></div>")]
  :before (.appendTo $el (js/$ "body"))

  "can be created in the element"
    (let [repl (shegon.repl/make-repl $el)]
      (doseq [param [:input :output :prompt]]
        (expect (instance? js/CodeMirror (param repl))))

      (expect (:$element repl) $el))

  :after (.remove $el))
