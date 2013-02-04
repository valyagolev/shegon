(ns shegon.examples
  (:require-macros [shegon.macros :as ms]))


(def -examples (atom []))

(defn add-example [descr f]
  (swap! -examples
    conj {:renderer f :descr descr}))

(defn call-example [ex]
  (try
    ((:renderer ex))
    (catch js/Error e e)))

(defn render-to [$el]
  (doseq [ex @-examples]
    (.appendTo (js/$ (str
      "<div class='example'>
        <div class='description'>" (:descr ex) "</div>
        <div class='result'>" (pr-str (call-example ex)) "</div>
      </div>")) $el)))


(ms/example
  (js/$ "<div>Hey</div>"))

(ms/example
  (+ 1 2))


