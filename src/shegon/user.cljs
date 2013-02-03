(ns shegon.user)


(defn log [& a]
  (shegon.repl/add-output (apply str a))
  (map #(js/console.log (clj->js %)) a))


(def $ js/$)

(defn js-map [& clauses]
  (clj->js (apply hash-map clauses)))

(defn load-module [module]
  (let [url (.-url module)
        provides (.-provides module)]
    (log "Loading " provides)

    (.ajax $ url (js-map :dataType "script"))

    ;; I've considered deleting modules before loading
    ; (str
    ;   (for [m provides :when (and (not= m "cljs.core")
    ;                               (not (nil? (try (js/eval m)
    ;                                               (catch js/Error e nil)))))]
    ;   (js/console.log m)
    ;   (str "delete " m ";")))

    ))

;; goog.provide doesn't allow us to reload modules :-(
(let [old (.-provide js/goog)]
  (set! js/goog.provide
    (fn [m]
      (try
        (old m)
        (catch js/Error e nil)))))


(defn require [& modules]
  (log "Loading asynchronously...")
  (.done
    (.ajax $ "/requires"
      (js-map :data {:modules modules} :type "post" :dataType "jsonp"))
    (fn [data] (doall (map load-module data)))))


