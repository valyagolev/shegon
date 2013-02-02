(ns shegon.user)

(let [old (.-provide js/goog)]
  (set! js/goog.provide (fn [m] (try (old m) (catch js/Error e nil)))))

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


(defn js-map [& clauses]
  (to-js (apply hash-map clauses)))

(defn load-module [module]
  ; returns a code to execute to kill
  (let [url (.-url module)
        provides (.-provides module)]
    (log "Loading " provides)
    ; (js/console.log (to-js provides))

    (.ajax $ url (to-js {:dataType "script"}))

    ; (str
    ;   (for [m provides :when (and (not= m "cljs.core")
    ;                               (not (nil? (try (js/eval m)
    ;                                               (catch js/Error e nil)))))]
    ;   (js/console.log m)
    ;   (str "delete " m ";")))

    ))

(defn require [& modules]
  (log "Loading asynchronously...")
  (.done
    (.ajax $ "/requires"
      (to-js {:data {:modules modules} :type "post" :dataType "jsonp"}))
    (fn [data] (doall (map load-module data)))))


