(ns shegon.repl
  (:require [shegon.user :as u]
            [shegon.history :as h]
            [shegon.evaluator :as eval])
  (:use [jayq.core :only [$ done]])
  (:require-macros [shegon.macros :as s]))


; (defn create-input []
;   (.on @input "change" #(h/changed-current (input-value))))


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


(defn- scroll-down [{:keys [$element]}]
  (.scrollTop $element (.-scrollHeight (first $element))))


(defn prompt-value []
  (str cljs.core/*ns* "=> "))


(defn- focus-only-input [{:keys [input]} cm]
  (.on cm "focus" (fn []
    (let [sel (.getSelection cm)]
      (when (= sel "")
        (js/setTimeout #(.focus input) 150))))))


(defn- last-pos [cm]
  (let [last-line (- (.lineCount cm) 1)]
    (clj->js {:line last-line
              :ch (.-length (.getLine cm last-line))})))


(defn print [{:keys [output]} value & [className]]
  (let [begin (last-pos output)]
    (.replaceRange output value begin)

    (when-not (nil? className)
      (.markText output begin (last-pos output)
        (clj->js {:className className})))))


(defn println [repl value & [className]]
  (print repl (str value "\n") className))


(defn get-output [{:keys [output]}]
  (.getValue output))


(defn set-input [{:keys [input]} value]
  (.setValue input value))


(defn eval-print [repl value]
  (done (eval/eval-cljs-deferred value)
    #(println repl (:result %) :user)))


(defn- make-repl* [$el]
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
    (.on input "change" #(scroll-down repl))
    (focus-only-input repl output)
    (focus-only-input repl prompt)
    repl))


(js/$ (fn []
  (when-let [repl-el ($ ".repl")]
    (def repl (make-repl repl-el))
    (println repl "o hai")
    (println repl "lol" :user)
    (println repl "lold"))

  ))





; (defn eval-print [code]
;   (u/eval code
;     #(do
;       (.setValue @prompt (prompt-value))
;       (add-output (or (:error %) (pr-str (:result %)))))))





; (defn input-value []
;   (.getValue @input))




; (defn current-prompt []
;   (.last ($ ".CodeMirror.prompt")))


; (defn format-input [prompt input]
;   (let [indent (.replace prompt (js* "/./g") " ")]
;     (str prompt (.replace input (js* "/\\n/g") (+ "\n" indent)))))

; (defn do-repl []
;   (let [inp (input-value)]
;     (h/add-input inp)
;     (add-output (format-input (prompt-value) inp) true)
;     (eval-print inp)
;     (.setValue @input "")))

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
