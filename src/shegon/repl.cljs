(ns shegon.repl
  (:require [shegon.user :as u])
  (:use [jayq.core :only [$ ajax]]))


(def input (atom nil))
(def $input (atom nil))

(declare do-repl)

(def input-keymap
  (u/js-map :Enter #(do-repl)))

(defn $repl-el [] ($ "#repl"))

(defn last-pos [cm]
  (let [last-line (- (.lineCount cm) 1)]
    (u/js-map :line last-line
              :ch (.-length (.getLine cm last-line)))))

(defn create-input []
  (reset! input
    (js/CodeMirror.
      (first ($repl-el))
      (u/js-map :value "(+ 1 2)"
                :extraKeys input-keymap)))
  (.focus @input)
  (.setCursor @input (last-pos @input))
  (reset! $input ($ (.getWrapperElement @input)))
  (.addClass @$input "input"))

(defn add-promt []
  (js/CodeMirror.
    #(.addClass (.insertBefore ($ %) @$input) "promt")
      (u/js-map :value "shegon.user=>"
                :readOnly true)))

(defn current-promt []
  (.last ($ ".CodeMirror.promt")))

(defn add-output [promt input output]
  (.on
    (js/CodeMirror.
      #(.addClass (.insertBefore ($ %) (shegon.repl/current-promt)) "output")
        (u/js-map :value (str promt "  " input "\n" output)
                  :readOnly true))
    "focus" focus-only-input))

(defn do-repl []
  (let [inp (.getValue @input)]
    (compile-js inp #(add-output "shegon.user=>" inp (do-eval %)))
    (.setValue @input "")))

(defn do-eval [code]
  (js/eval code))

(defn compile-js [code callback]
  (let [pr (ajax "/compiler" {:type :post
                              :data {:source code}
                              :dataType :jsonp})]
    (.done pr (fn [data] (callback (.-result data))))
    (.fail pr (fn [error] (js/console.log error)))))

(defn focus-only-input [cm]
  (let [sel (.getSelection cm)]
    (when (= sel "")
      (js/setTimeout #(.focus @input) 100))))

($ (fn []

    (.html ($repl-el) "")

    (create-input)
    (add-promt)
    (add-output "(help)")

    ))
