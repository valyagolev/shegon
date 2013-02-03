(ns shegon.compiler
  (:require [cljs.compiler :as comp]
            [cljs.analyzer :as ana]))


(defn catching-requires-emit [a]
  (println
    (let [js (with-out-str (comp/emit a))]
      (if (= (:op a) :ns)
        (str "try{" js "}catch(e){};") ; to hell with goog.require throwing stuff
        js))))


(defn clojure-to-js-emit [s]
  (comp/with-core-cljs
    (binding [ana/*cljs-ns* 'shegon.user
              ana/*cljs-file* "<fazil REPL>"]

      (when (nil? (ana/get-namespace ana/*cljs-ns*))
        (ana/set-namespace ana/*cljs-ns* {:name ana/*cljs-ns*}))

      (binding [ana/*cljs-ns* 'cljs.core]
        (comp/emit (ana/analyze
          (assoc (ana/empty-env) :ns (ana/get-namespace ana/*cljs-ns*) :context :statement)
          `(def ~'*ns* '~ana/*cljs-ns*))))

      (let [r (java.io.StringReader. s)
            ; env (setup/load-core-names)
            ; env (assoc-in (ana/empty-env) [:locals '*ns*] ana/*cljs-ns*)
            env (assoc (ana/empty-env) :ns (ana/get-namespace ana/*cljs-ns*) :context :expr)
            pbr (clojure.lang.LineNumberingPushbackReader. r)
            eof (Object.)]

        ;; setting *ns* to ns because we can? should we?
        ; (binding [ana/*cljs-ns* 'cljs.core]
        ;   (comp/emit
        ;     (ana/analyze (assoc (ana/empty-env) :ns (ana/get-namespace ana/*cljs-ns*))
        ;     `(~'def ~'*ns* '~ana/*cljs-ns*))))

        (loop [r (read pbr false eof false)]
          (if (identical? eof r)
            {:ns ana/*cljs-ns*}
            (do
              (comp/emit (ana/analyze env r))
              (recur (read pbr false eof false)))))))))

(defn clojure-to-js [s]
  (let [retn (atom nil)
        result (with-out-str (reset! retn (clojure-to-js-emit s)))]
    (assoc @retn :result result)))

; (defn dependencies [js-str]
;   (closure/cljs-dependencies {:output-dir "resources/public"} ["jayq.core"]))


(defn compile-js [{:keys [source]}]
    (try (clojure-to-js source)
        (catch Exception e {:exception e})))
