(ns shegon.evaluator
  (:use [jayq.core :only [$ ajax attr]]))

(defn anti-forgery-token []
  (attr ($ "#__anti-forgery-token") "value"))

(defn compile-cljs-deferred [code]
  (ajax {:url "/compiler"
         :type :post
         :data {:source               code
                :ns                   cljs.core/*ns*
                :__anti-forgery-token (anti-forgery-token)}
         :dataType :jsonp}))
