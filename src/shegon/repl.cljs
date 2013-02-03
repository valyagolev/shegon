(ns shegon.repl
  (:require [shegon.user :as u])
  (:use [jayq.core :only [$ ajax]]))



(def input (atom nil))
(def $input (atom nil))

(declare do-repl)

(defn compile-cljs [code callback]
  (let [pr (ajax "/compiler" {:type :post
                              :data {:source code}
                              :dataType :jsonp})]
    (.done pr (fn [data] (callback {:result (.-result data)
                                    :error (.-exception data)})))
    (.fail pr (fn [error] (callback {:error error})))))

(defn js-eval [code]
  (try
    {:result (js/eval code)}
    (catch js/Error e {:error (.-stack e)})))

(defn cljs-eval [code]
  (compile-cljs code
    #(do (add-output (:error %))
         (when-let [code (:result %)]
            (let [eres (js-eval code)]
              (add-output (:error eres))
              (add-output (:result eres)))))))


(def input-keymap
  (u/js-map :Enter #(do-repl)
            :Ctrl-Enter "newlineAndIndent"
            :Alt-Enter "newlineAndIndent"))

(defn $repl-el [] ($ "#repl"))
(defn scroll-down [] (.scrollTop ($repl-el) (.-scrollHeight (first ($repl-el)))))

(defn last-pos [cm]
  (let [last-line (- (.lineCount cm) 1)]
    (u/js-map :line last-line
              :ch (.-length (.getLine cm last-line)))))

(defn create-input []
  (reset! input
    (js/CodeMirror.
      (first ($repl-el))
      (u/js-map :value "(+ 1 2)"
                :extraKeys input-keymap
                :mode "clojure")))
  (.focus @input)
  (.setCursor @input (last-pos @input))
  (reset! $input ($ (.getWrapperElement @input)))
  (.addClass @$input "input")
  (.on @input "change" scroll-down))

(defn add-prompt []
  (js/CodeMirror.
    #(.addClass (.insertBefore ($ %) @$input) "prompt")
      (u/js-map :value "shegon.user=>"
                :readOnly true)))

(defn current-prompt []
  (.last ($ ".CodeMirror.prompt")))

(defn add-output [output]
  (when output
    (.on
      (js/CodeMirror.
        #(.addClass (.insertBefore ($ %) (shegon.repl/current-prompt)) "output")
          (u/js-map :value (str output)
                    :readOnly true))
      "focus" focus-only-input)
    (scroll-down)))

(defn format-input [prompt input]
  (let [indent (.replace prompt (js* "/./g") " ")]
    (str prompt (.replace input "\n" (+ "\n" indent)))))

(defn do-repl []
  (let [inp (.getValue @input)]
    (add-output (format-input "shegon.user=>  " inp))
    (cljs-eval inp)
    (.setValue @input "")))

(defn focus-only-input [cm]
  (let [sel (.getSelection cm)]
    (when (= sel "")
      (js/setTimeout #(.focus @input) 100))))

($ (fn []

    (.html ($repl-el) "")

    (create-input)
    (add-prompt)
    (add-output "(help)")

    ))
