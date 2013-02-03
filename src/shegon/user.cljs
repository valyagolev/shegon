(ns shegon.user
  (:use [jayq.core :only [ajax]]))

(defn log [& a]
  (when
    shegon.repl/add-output
    (shegon.repl/add-output (apply str a)))
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

//          setTimeout(function() {
//            shegon.user.log('Loaded ' + m);
//          }, 500); // should be enough to get shegon.user loaded
        };

      };
      "))

(defn require [& modules]
  (log "Loading asynchronously...") ; Open the console if it never says loaded.")
  (patch-provide)
  (.done
    (ajax "/requires" {:data {:modules modules}
                       :type "post"
                       :dataType "jsonp"})
    (fn [data] (doall (map load-module data)))))


(defn compile-str [code callback]
  (let [pr (ajax "/compiler" {:type :post
                              :data {:source code}
                              :dataType :jsonp})]
    (.done pr (fn [data] (callback {:result (.-result data)
                                    :error (.-exception data)})))
    (.fail pr (fn [error] (callback {:error error})))))


(defn emit-js [code]
  (compile-str code #(log (or (:error %) (:result %)))))

(defn eval-js [code]
  (try
    {:result (js/eval code)}
    (catch js/Error e {:error (.-stack e)})))

(defn eval [code callback]
  (compile-str code
    #(callback (if (:result %)
                   (eval-js (:result %))
                   %))))



(defn help []
  (log
"Hey! This is Shegon ClojureScript REPL talking.

All the obvious clojure stuff is probably working.

Try also stuff from shegon.user namespace:
  (log \"hello\")                 ; to write stuff down in the console

  (emit-js \"(+ 1 1)\")           ; note that as this op and the next are
  (compile-str \"(+ 1 1)\" log)   ; async operations. in the second
                                  ; case you can supply a callback

  (require 'your.module)          ; compiles and (re-)loads your module
                                  ; very nice!"))

