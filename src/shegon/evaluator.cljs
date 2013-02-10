(ns shegon.evaluator
  (:use [jayq.core :only [$ attr ajax then $deferred reject]]))


(defn anti-forgery-token []
  (attr ($ "#__anti-forgery-token") "value"))

(defn bind-deferred
  ([deferred right] (bind-deferred deferred right identity))
  ([deferred right left]
    (then deferred right left)))


(defn compile-cljs-deferred [code]
  (bind-deferred
    (ajax {:url "/compiler"
           :type :post
           :data {:source               code
                  :ns                   cljs.core/*ns*
                  :__anti-forgery-token (anti-forgery-token)}
           :dataType :jsonp})
    (fn [data]
      (if-let [error (.-exception data)]
        (reject ($deferred) error)
        {:js-code (.-result data)
         :ns (.-ns data)}))))


(defn eval-cljs-deferred [code]
  (bind-deferred (compile-cljs-deferred code)
    (fn [{:keys [js-code] :as data}]
      (assoc data :result (js/eval js-code)))))
