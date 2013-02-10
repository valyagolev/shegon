(ns shegon.test.repl
  (:require [shegon.repl])
  (:require-macros [shegon.test.macros :as t]))

(t/describe "REPL again" [$el (js/$ "<div></div>")]
  :before (.appendTo $el (js/$ "body"))

  "can be created in the element"
    (let [repl (shegon.repl/make-repl $el)]
      (doseq [x [:input :output :prompt]]
        (t/expect (instance? js/CodeMirror (:input repl))))

      (t/expect (:$element repl) $el))

  :after (.remove $el))
