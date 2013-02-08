(ns shegon.user
  (:use [jayq.core :only [ajax]]))

; that feels wrong
(set! cljs.core/*ns* 'shegon.user)

(defn log [& a]
  (when
    shegon.repl/add-output
    (shegon.repl/add-output (apply str a)))
  (map #(js/console.log (clj->js %)) a))

(defn js-map [& clauses]
  (clj->js (apply hash-map clauses)))

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
//            if (goog.isProvided_(m))
//              eval('delete ' + m);

            old_provide(m);
          } catch (e) {
//            console.log('Error loading ', m, e);
//            debugger;
          }

//          setTimeout(function() {
//            shegon.user.log('Loaded ' + m);
//          }, 500); // should be enough to get shegon.user loaded
        };

      };
      "))

(js/$
  (fn []
    (def anti-forgery-token
      (.attr (js/$ "#__anti-forgery-token") "value"))))

(defn load-module [module]
  (let [url (.-url module)
        provides (.-provides module)]
    (log "Loading " provides)

    (ajax url {:dataType "script"})))

(defn require [& modules]
  (log "Loading asynchronously...") ; Open the console if it never says loaded.")
  (.done
    (ajax "/requires" {:data {:modules modules
                              :__anti-forgery-token anti-forgery-token}
                       :type "post"
                       :dataType "jsonp"})
    (fn [data] (doall (map load-module data)))))


(defn compile-str [code callback]
  (let [pr (ajax "/compiler" {:type :post
                              :data {:source code
                                     :ns cljs.core/*ns*
                                     :__anti-forgery-token anti-forgery-token}
                              :dataType :jsonp})]
    (.done pr (fn [data] (callback {:result (.-result data)
                                    :error (.-exception data)
                                    :ns (.-ns data)})))
    (.fail pr (fn [error] (callback {:error error}))))
  nil)


(defn emit-js [code]
  (compile-str code #(log (or (:error %) (:result %)))))

(defn eval-js [code]
  (try
    {:result (js/eval code)}
    (catch js/Error e {:error (.-stack e)})))

(defn eval [code callback]
  (compile-str code (fn [{:keys [result ns] :as compile_result}]
    (if result
      (let [retval (eval-js result)]
        (when ns
          (set! cljs.core/*ns* (symbol ns)))
        (callback (merge compile_result retval)))
      (callback compile_result)))))

(defn help []
  (log
"Hey! This is Shegon ClojureScript REPL talking.

All the obvious clojure stuff is probably working. The history is saved even
between sessions. Life is good. You look nice. Let's dance more!

Try also stuff from shegon.user namespace:
  (log \"hello\")                   ; to write stuff down in the console

  (emit-js \"(+ 1 1)\")             ; note that this op and the next one are
  (compile-str \"(+ 1 1)\" log)     ; async operations. in the second
                                  ; case you can supply a callback

  (ns some.ns)                    ; changes ns where you're in
                                  ; (I hope it makes sense, didn't figure
                                  ; Clojure namespaces completely yet)

  (require 'your.module)          ; compiles and (re-)loads your module
                                  ; from .cljs file on your classpath
                                  ; (note that classpath is not a
                                  ;   :cljsbuild :builds :source-paths
                                  ; thing but rather like clojure classpath.
                                  ; By default it's src/)
                                  ; very nice!

This thingy is open-source: https://github.com/va1en0k/shegon"))


(patch-provide)
