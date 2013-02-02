(ns shegon.user)


(defn log [& a]
  (js/window.shegon.logToConsole (apply str a)))

(def $ js/$)


(defn to-js
  "makes a javascript map from a clojure one"
  [obj]
  (cond (map? obj) (let [out (js-obj)]
                        (doall (map
                                #(aset out (to-js (first %)) (to-js (second %)))
                                obj))
                        out)
        (seq? obj) (let [out (js* "[]")] (reduce #(.push out (to-js %2)) nil obj) out)
        (or (keyword? obj) (symbol? obj)) (name obj)
        :else           obj))


(defn load-module [module]
  (let [url (.-url module)
        provides (.-provides module)]
    (log "Loading " url " : " provides)
    ; (js/console.log (to-js provides))

    (.ajax $ url (to-js {:dataType "script"}))

    (doseq [m provides :when (not= m "cljs.core")]
      (js/console.log m)
      (js/eval (str "delete " m ";")))))

(defn require [& modules]
  (log "Loading...")
  (.done
    (.ajax $ "/requires"
      (to-js {:data {:modules modules} :type "post" :dataType "jsonp"}))
    (fn [data] (doall (map load-module data)))))


; (defn repl-async-op [f]
;   (fn [& args] (apply f (js/shegon.startAsync))))

; (def require (repl-async-op require*))


