(ns shegon.test.utils
  (:use [jayq.core :only [$deferred then resolve]]))


(defn deferred-to-either [deferred]
  (then deferred #(hash-map :result %)
                 #(resolve ($deferred) {:error %})))


(defn timeout-deferred [ms]
  (let [d ($deferred)]
    (js/setTimeout #(resolve d) ms)
    d))
