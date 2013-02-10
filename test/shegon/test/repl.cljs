(ns shegon.test.repl
  (:require [shegon.repl]))

(js/describe "REPL" (fn[]
  (let [$el (js/$ "<div></div>")]
    (js/beforeEach (fn []
      (.appendTo $el (js/$ "body"))))
    (js/it "can be created in the element" (fn []
      (let [repl (shegon.repl/make-repl $el)]
        (.toBe (js/expect (:$element repl)) $el))))
    (js/afterEach (fn []
      (.remove $el))))))
