(ns shegon.examples
  (:require [dommy.template :as template]))


(def -examples (atom []))

(defn add-example [descr f]
  (swap! -examples
    conj {:renderer f :descr descr}))

(defn call-example [ex]
  (try
    ((:renderer ex))
    (catch js/Error e e)))

(defn render-example [ex]
  (template/node
    [:div.example
      [:div.description (:descr ex)]
      [:div.result (pr-str (call-example ex))]]))


(defn render-to [$el]
  (doseq [ex @-examples]
    (.append $el (render-example ex))))



;
; (shegon.macros/example
;   (js/$ "<div>Hey</div>"))

; (shegon.macros/example
;   (+ 1 2))


