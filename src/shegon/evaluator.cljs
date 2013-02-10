(ns shegon.evaluator
  (:use [jayq.core :only [$ attr ajax then $deferred reject resolve]]))


(defn anti-forgery-token []
  (attr ($ "#__anti-forgery-token") "value"))


(defn return-deferred [value]
  (resolve ($deferred) value))


(defn fail-deferred [error]
  (reject ($deferred) error))


(defn bind-deferred [deferred right & [left]]
  (then deferred right (or left identity)))


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
        (fail-deferred error)
        {:js-code (.-result data)
         :ns (.-ns data)}))))


(defn eval-deferred [code]
  (try
    (js/console.log code)
    (return-deferred (js/eval code))
    (catch js/Error e (fail-deferred e))))


(defn eval-cljs-deferred [code]
  (bind-deferred (compile-cljs-deferred code)
    (fn [{:keys [js-code] :as data}]
      (bind-deferred (eval-deferred js-code)
        #(assoc data :result %)))))
