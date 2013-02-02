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
    (binding [ana/*cljs-ns* 'cljs.user
            ana/*cljs-file* "<fazil REPL>"]
      (let [r (java.io.StringReader. s)
            ; env (setup/load-core-names)
            env (ana/empty-env)
            pbr (clojure.lang.LineNumberingPushbackReader. r)
            eof (Object.)]
        (loop [r (read pbr false eof false)]
          (let [env (assoc env :ns (ana/get-namespace ana/*cljs-ns*))]
            (when-not (identical? eof r)
              ; (catching-requires-emit (ana/analyze env r))
              (comp/emit (ana/analyze env r))
              (recur (read pbr false eof false)))))))))

(defn clojure-to-js [s]
  (with-out-str (clojure-to-js-emit s)))

(defn compile-js [{:keys [source]}]
    (try {:result (clojure-to-js source)}
        (catch Exception e {:exception e})))
