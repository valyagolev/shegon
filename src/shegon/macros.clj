(ns shegon.macros)


(defmacro with-codemirror [name params & body]
  `(let [retn#  (atom nil)
         cm#    (js/CodeMirror.
                  (fn [~name]
                    (reset! retn# (do ~@body))
                    ~(when-let [cls (:class params)]
                      `(.addClass (js/$ ~name) ~cls)))
                  (~'clj->js ~params))]

    (assoc @retn# ~(keyword name) cm#)))


(defmacro with-codemirrors [topname bindings & body]
  (reduce
    (fn [body [name params]] `(with-codemirror ~name ~params ~body))
    `(do {~(keyword topname) ~@body})
    (partition 2 bindings)))
