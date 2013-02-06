(ns shegon.server
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]

            [clj-stacktrace.repl]

            [shegon.compiler]
            [shegon.namespaces])
  (:use [hiccup.page :only [include-css include-js html5]]))


(defn layout [& content]
  (html5
    [:head
     [:title "shegon"]
     (include-css "/resources/codemirror/codemirror.css"
                  "/resources/css/reset.css"
                  "/resources/css/shegon.css")
     (include-js "/resources/codemirror/codemirror.js"
                 "/resources/codemirror/clojure.js"
                 "/resources/codemirror/javascript.js"
                 "/resources/codemirror/matchbrackets.js"
                 "/resources/js/jquery-1.9.0.min.js"
                 "/resources/js/shegon-bootstrap.js")]
    [:body
     [:div#wrapper
      content]]))

(defn map-key [m k f]
  (if-let [v (k m)]
    (assoc m k (f v))
    m))

(defn jsonp-or-json [data callback]
  {:status 200
   :headers {}
   :body (let [json (json/write-str data)]
          (if callback
            (str callback "(" json ")")
            (str json)))})


(defroutes app-routes
  (GET "/" [] (layout [:div#repl]))

  (POST "/requires" {{:keys [callback modules]} :params}
    (jsonp-or-json (shegon.namespaces/load-modules modules) callback))

  (POST "/compiler" {{:keys [callback] :as params} :params}
    (jsonp-or-json
      (map-key (shegon.compiler/compile-js params)
        :exception clj-stacktrace.repl/pst-str)
      callback))


  (GET ["/_resources/:name" :name #".*"] [name]
    {:status 200
     :headers {}
     :body (.getContent (clojure.java.io/resource name))})

  (route/resources "/resources/"))

(def runserver
  (handler/site app-routes))
