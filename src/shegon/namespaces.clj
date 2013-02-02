(ns shegon.namespaces
  (:require [cljs.closure :as cc]
            [clojure.string :as string]
            [cljs.compiler :as comp]
            [cljs.analyzer :as ana]
            [clojure.java.io :as io]
            ))

(defn public-output-path [] (.getCanonicalPath (java.io.File. "resources/public/_compiled")))


(defn url-path-for-module [{:keys [url]}]
  (clojure.string/replace (.getPath url) (public-output-path) "_compiled"))


(defn load-modules [modules]
  ;; cljs-dependencies compiles everything needed <3
  (map #(assoc % :url (url-path-for-module %))
    (cc/dependency-order
      (cc/cljs-dependencies
        {:output-dir (public-output-path)} modules))))




