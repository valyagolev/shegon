(ns shegon.evaluator
  (:use [jayq.core :only [$ attr ajax pipe]]))


(defn anti-forgery-token []
  (attr ($ "#__anti-forgery-token") "value"))


(defn compile-cljs-deferred [code]
  (pipe (ajax {:url "/compiler"
               :type :post
               :data {:source               code
                      :ns                   cljs.core/*ns*
                      :__anti-forgery-token (anti-forgery-token)}
               :dataType :jsonp})
        (fn [data]
          {:result (.-result data)
           :error (.-exception data)
           :ns (.-ns data)})))


(defn eval-cljs-deferred [code]
  (pipe (compile-cljs-deferred code)
    (fn [{:keys [result] :as data}]
      (assoc data :result (js/eval result)))))
