(ns shegon.namespaces
  (:require [cljs.closure :as cc]
            [clojure.string :as string]
            [clojure.java.io :as io]
            ))

(defn public-output-path [] (.getCanonicalPath (java.io.File. "resources/public/_compiled")))

(defn url-path-for-module [{:keys [url file]}]
  (cond
    file (str "/_resource/" file)
    url  (clojure.string/replace (.getPath url) (public-output-path) "/_compiled")))


(defn load-modules [requires]
  ;; cljs-dependencies compiles everything needed <3
  (map #(assoc % :url (url-path-for-module %))
    (cc/dependency-order
      (let [opts {:output-dir (public-output-path)}
            required-cljs (cc/cljs-dependencies opts requires)
            required-js (cc/js-dependencies opts (set (concat (mapcat :requires required-cljs) requires)))]
        (concat required-js required-cljs)))))


