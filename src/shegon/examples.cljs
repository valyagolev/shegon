(ns shegon.examples)


(def -examples (atom []))

(defn example [f] (swap! -examples conj f))

(defn call-example [ex]
  (try
    (ex)
    (catch js/Error e e)))

(defn render-to [$el]
  (doseq [ex @-examples]
     (.appendTo (js/$ (str "<div class='example'>" (pr-str (call-example ex)) "</div>")) $el)))

(example (fn [] (js/$ "<div>Hey</div>")))
(example (fn [] (+ 1 2)))

