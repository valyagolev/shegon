(ns shegon.repl
  (:require [shegon.user :as u]
            [shegon.history :as h])
  (:use [jayq.core :only [$]]))

(def input (atom nil))
(def $input (atom nil))

(def prompt (atom nil))


(declare do-repl)

(defn prompt-value []
  (str cljs.core/*ns* "=> "))

(defn eval-print [code]
  (u/eval code
    #(do
      (.setValue @prompt (prompt-value))
      (add-output (or (:error %) (pr-str (:result %)))))))


(def input-keymap
  (u/js-map :Enter #(do-repl)
            :Ctrl-Enter "newlineAndIndent"
            :Alt-Enter "newlineAndIndent"
            :Up #(history-move :up)
            :Down #(history-move :down)))

(defn $repl-el [] ($ "#repl"))
(defn scroll-down [] (.scrollTop ($repl-el) (.-scrollHeight (first ($repl-el)))))

(defn last-pos [cm]
  (let [last-line (- (.lineCount cm) 1)]
    (u/js-map :line last-line
              :ch (.-length (.getLine cm last-line)))))

(defn input-value []
  (.getValue @input))

(defn create-input []
  (reset! input
    (js/CodeMirror.
      (first ($repl-el))
      (u/js-map :value ""
                :extraKeys input-keymap
                :mode "clojure"
                :matchBrackets true)))
  (.focus @input)
  (.setCursor @input (last-pos @input))
  (reset! $input ($ (.getWrapperElement @input)))
  (.addClass @$input "input user")
  (.on @input "change" scroll-down)
  (.on @input "change" #(h/changed-current (input-value))))

(defn create-prompt []
  (reset! prompt
    (js/CodeMirror.
      #(.addClass (.insertBefore ($ %) @$input) "prompt user")
        (u/js-map :value (prompt-value)
                  :readOnly true))))

(defn current-prompt []
  (.last ($ ".CodeMirror.prompt")))

(defn add-output [output & [user?]]
  (when output
    (.on
      (js/CodeMirror.
        #(.addClass
            (.insertBefore ($ %) (shegon.repl/current-prompt))
            (str "output" (when user? " user")))
          (u/js-map :value (str output)
                    :readOnly true))
      "focus" focus-only-input)
    (scroll-down)))

(defn format-input [prompt input]
  (let [indent (.replace prompt (js* "/./g") " ")]
    (str prompt (.replace input (js* "/\\n/g") (+ "\n" indent)))))

(defn do-repl []
  (let [inp (input-value)]
    (h/add-input inp)
    (add-output (format-input (prompt-value) inp) true)
    (eval-print inp)
    (.setValue @input "")))

(defn focus-only-input [cm]
  (let [sel (.getSelection cm)]
    (when (= sel "")
      (js/setTimeout #(.focus @input) 100))))

(defn history-move [direction]
  (.setValue @input (h/move direction))
  (.setCursor @input (last-pos @input)))

(defn render-examples []
  (e/render-to
    (.empty ($right-panel))))

(defn log-onerror []
  (set! js/window.onerror (fn [exc src line] (u/log "Top level exception: \n  " exc " at " src ":" line))))

($ (fn []
    (.html ($repl-el) "")

    (create-input)
    (create-prompt)
    (u/log "Wait for it...")
    (eval-print "(help)")
    (log-onerror)))
