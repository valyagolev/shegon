(ns shegon.user
  (:use [jayq.core :only [ajax]]))

(defn log [& a]
  (shegon.repl/add-output (apply str a))
  (map #(js/console.log (clj->js %)) a))

(defn js-map [& clauses]
  (clj->js (apply hash-map clauses)))

(defn load-module [module]
  (let [url (.-url module)
        provides (.-provides module)]

    (log "Loading " provides)

    (ajax url {:dataType "script"})))

;; goog.provide doesn't allow us to reload modules :-(
;; can't be sure even cljs.core is loaded there :/
;; so have to write js
(def patch-provide (js* "function(){
        if (window._provide_patched)
          return;

        window._provide_patched = true;

        console.log('patching goog.provide');
        var old_provide = goog.provide;
        goog.provide = function(m) {
          try {
            if (goog.isProvided_(m))
              eval('delete ' + m);

            old_provide(m);
          } catch (e) {
            console.log('Error loading ', m, e);
            debugger;
          }
        };

      };
      "))

(defn require [& modules]
  (log "Loading asynchronously...")
  (patch-provide)
  (.done
    (ajax "/requires" {:data {:modules modules}
                       :type "post"
                       :dataType "jsonp"})
    (fn [data] (doall (map load-module data)))))


