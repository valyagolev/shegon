(ns shegon.namespaces
  (:require [cljs.closure :as cc]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [leiningen.core.user]))

(def public-output-path
  (atom
    (.getAbsolutePath
      (io/file (leiningen.core.user/leiningen-home) "shegon/_compiled"))))

(defn url-path-for-module [{:keys [url file]}]
  (str "http://127.0.0.1:19000"
    (cond
      file (str "/_resources/" file)
      url  (clojure.string/replace (.getPath url) @public-output-path "/_compiled"))))


(defn load-modules [requires]
  ;; cljs-dependencies compiles everything needed <3
  (map #(assoc % :url (url-path-for-module %))
    (cc/dependency-order
      (let [opts {:output-dir @public-output-path}
            required-cljs (cc/cljs-dependencies opts requires)
            required-js (cc/js-dependencies opts (set (concat (mapcat :requires required-cljs) requires)))]
        (concat required-js required-cljs)))))


