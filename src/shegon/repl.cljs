(ns shegon.repl
  (:require [shegon.user :as u]
            [shegon.history :as h]
            [dommy.template :as template])
  (:use [jayq.core :only [$]])
  (:require-macros [shegon.macros :as s]))

; (defn create-input []
;   (.on @input "change" #(h/changed-current (input-value))))

(defn scroll-down [{:keys [$element]}]
  (.scrollTop $element (.-scrollHeight (first $element))))


(defn prompt-value []
  (str cljs.core/*ns* "=> "))

(def cm-input-params
  {:value ""
   :mode "clojure"
   :matchBrackets true
   :class "input user"
   :extraKeys {; :Enter #(do-repl)
                :Ctrl-Enter "newlineAndIndent"
                :Alt-Enter "newlineAndIndent"
                ; :Up #(history-move :up)
                ; :Down #(history-move :down)
                }})


(defn make-repl* [$el]
  (s/with-codemirrors $element
    [output {:mode "clojure"       :readOnly true}
     prompt {:value (prompt-value) :readOnly true :class "prompt user"}
     input  cm-input-params]
      (-> $el
          (.html "")
          (.append output)
          (.append prompt)
          (.append input))))


(defn make-repl [$el]
  (let [{:keys [$element output prompt input] :as repl}
          (make-repl* $el)]
    (.focus input)
    (.on input "change" #(scroll-down repl))))


(js/$ (fn []

  (def repl (make-repl (-> ".repl" $ (nth 0))))

  (js/console.log (clj->js repl))))





; (defn eval-print [code]
;   (u/eval code
;     #(do
;       (.setValue @prompt (prompt-value))
;       (add-output (or (:error %) (pr-str (:result %)))))))




; (defn $repl-el [] ($ "#repl"))
;

; (defn last-pos [cm]
;   (let [last-line (- (.lineCount cm) 1)]
;     (u/js-map :line last-line
;               :ch (.-length (.getLine cm last-line)))))

; (defn input-value []
;   (.getValue @input))




; (defn current-prompt []
;   (.last ($ ".CodeMirror.prompt")))

; (defn add-output [output & [user?]]
;   (when output
;     (.on
;       (js/CodeMirror.
;         #(.addClass
;             (.insertBefore ($ %) (shegon.repl/current-prompt))
;             (str "output" (when user? " user")))
;           (u/js-map :value (str output)
;                     :readOnly true))
;       "focus" focus-only-input)
;     (scroll-down)))

; (defn format-input [prompt input]
;   (let [indent (.replace prompt (js* "/./g") " ")]
;     (str prompt (.replace input (js* "/\\n/g") (+ "\n" indent)))))

; (defn do-repl []
;   (let [inp (input-value)]
;     (h/add-input inp)
;     (add-output (format-input (prompt-value) inp) true)
;     (eval-print inp)
;     (.setValue @input "")))

; (defn focus-only-input [cm]
;   (let [sel (.getSelection cm)]
;     (when (= sel "")
;       (js/setTimeout #(.focus @input) 100))))

; (defn history-move [direction]
;   (.setValue @input (h/move direction))
;   (.setCursor @input (last-pos @input)))

; (defn render-examples []
;   (e/render-to
;     (.empty ($right-panel))))

; (defn log-onerror []
;   (set! js/window.onerror (fn [exc src line] (u/log "Top level exception: \n  " exc " at " src ":" line))))

; ($ (fn []
;     (.html ($repl-el) "")

;     (create-input)
;     (create-prompt)
;     (u/log "Wait for it...")
;     (eval-print "(help)")
;     (log-onerror)))
