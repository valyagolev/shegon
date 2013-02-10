(ns shegon.test.evaluator
  (:require [shegon.evaluator :as eval])
  (:use-macros [shegon.test.macros :only [describe expect async-test]])
  (:use [jayq.core :only [$deferred then resolve]]))

(defn deferred-to-either [deferred]
  (then deferred #(hash-map :result %)
                 #(resolve ($deferred) {:error %})))



(describe "Evaluator"
  :it "can compile clojurescript"
    (async-test 200
      [result (eval/compile-cljs-deferred "(+ 1 2)")]
      (expect (:js-code result) "(1 + 2)"))

  :it "reports errors on wrong clojurescript"
    (async-test 200
      [result (deferred-to-either (eval/compile-cljs-deferred "(+ 1 2"))]
      (expect (string? (:error result))))

  :it "can eval clojurescript"
    (async-test 200
      [result (eval/eval-cljs-deferred "(+ 1 2)")]
      (expect (:result result) 3))

  :it "reports exceptions from eval"
    (async-test 200
      [result (deferred-to-either (eval/eval-cljs-deferred "(undefined-function 123)"))]
      (expect (instance? js/Error (:error result))))

  :it "evals async javascript as async"
    (async-test 200
      [result (eval/eval-cljs-deferred "(let [d (jayq.core/$deferred)]
                                          (js/setTimeout #(jayq.core/resolve d 123) 100) d)")]
      (expect (:result result) 123)))
