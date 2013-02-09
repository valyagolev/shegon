(ns shegon.compiler
  (:require [cljs.compiler :as comp]
            [cljs.analyzer :as ana]))


(defn- clojure-to-js-emit [s ns]
  (comp/with-core-cljs
    (binding [ana/*cljs-ns* ns
              ana/*cljs-file* "<fazil REPL>"]

      (when (nil? (ana/get-namespace ana/*cljs-ns*))
        (ana/set-namespace ana/*cljs-ns* {:name ana/*cljs-ns*}))

      ; telling compiler – but not interpreter! – about *ns*
      (binding [ana/*cljs-ns* 'cljs.core]
        (ana/analyze
          (assoc (ana/empty-env) :ns (ana/get-namespace ana/*cljs-ns*) :context :statement)
          `(def ~'*ns* ns)))

      (let [r (java.io.StringReader. s)
            env (assoc (ana/empty-env) :ns (ana/get-namespace ana/*cljs-ns*) :context :expr)
            pbr (clojure.lang.LineNumberingPushbackReader. r)
            eof (Object.)]

        (loop [r (read pbr false eof false)]
          (if (identical? eof r)
            {:ns ana/*cljs-ns*}
            (do
              (comp/emit (ana/analyze env r))
              (recur (read pbr false eof false)))))))))


(defn- clojure-to-js [s ns]
  (let [retn (atom nil)
        result (with-out-str (reset! retn (clojure-to-js-emit s ns)))]
    (assoc @retn :result result)))


(defn compile-js
  "Compiles ClojureScript staments (from string) to JavaScript.

  In case of exception, returns {:exception ...}, because I hate exceptions.

  user=> (compile-js {:source \"(+ x 5)\"})
  {:result \"(shegon.user.x + 5)\", :ns shegon.user}

  user=> (compile-js {:source \"(def a (+ b 5))\" :ns 'hello})
  {:result \"hello.a = (hello.b + 5)\", :ns hello}

  user=> (compile-js {:source \"(ns change-ns)\"})
  {:result \"goog.provide('change_ns');\\ngoog.require('cljs.core');\\n\", :ns change-ns}

  user=> (compile-js {:source \"((no octocat you don't have matching parens\"})
  {:exception #<ReaderException clojure.lang.LispReader$ReaderException: java.lang.RuntimeException: EOF while reading, starting at line 1>}"
  [{:keys [source ns]}]
    (let [ns (or ns 'shegon.user)]
      (try (clojure-to-js source (symbol ns))
        (catch Exception e {:exception e}))))
