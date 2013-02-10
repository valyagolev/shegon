(ns shegon.test.evaluator
  (:require [shegon.evaluator :as eval])
  (:use-macros [shegon.test.macros :only [describe expect async-test]]))

(describe "Evaluator"
  :it "can compile javascript"
    (async-test 200
      [result (eval/compile-cljs-deferred "(+ 1 2)")]
      (expect (.-result result) "(1 + 2)")))
