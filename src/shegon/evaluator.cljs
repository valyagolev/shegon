(ns shegon.evaluator
  (:use [jayq.core :only [$ attr ajax then $deferred reject]]))


(defn anti-forgery-token []
  (attr ($ "#__anti-forgery-token") "value"))


(defn compile-cljs-deferred [code]
  (then (ajax {:url "/compiler"
               :type :post
               :data {:source               code
                      :ns                   cljs.core/*ns*
                      :__anti-forgery-token (anti-forgery-token)}
               :dataType :jsonp})
        (fn [data]
          (if-let [error (.-exception data)]
            (reject ($deferred) error)
            {:result (.-result data)
             :ns (.-ns data)}))
        identity))


(defn eval-cljs-deferred [code]
  (then (compile-cljs-deferred code)
    (fn [{:keys [result] :as data}]
      (assoc data :result (js/eval result)))
    identity))
