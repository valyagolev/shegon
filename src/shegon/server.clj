(ns shegon.server
  (:require [clojure.data.json :as json]

            [ring.adapter.jetty]
            [ring.middleware reload anti-forgery session params]
            [ring.middleware.session.cookie]
            [ring.util.anti-forgery]

            [compojure.route :as route]
            [compojure.handler :as handler]

            [clj-stacktrace.repl]

            ; [shegon.security]

            [shegon.compiler]
            [shegon.namespaces])
  (:use [hiccup.page :only [include-css include-js html5]]
        [hiccup.core :only [html]]
        [compojure.core]))


(println "Code reloaded...")


(defonce running-server (atom nil))
(declare run-if-not-running)


(defn include-cljs [& modules]
  (run-if-not-running)

  (html
    (include-js "http://127.0.0.1:19000/_resources/goog/base.js")
    (map #(include-js (:url %)) (shegon.namespaces/load-modules modules))))


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
                 "/resources/js/jquery-1.9.0.min.js")
     (include-cljs "shegon.repl")]
    [:body
     (ring.util.anti-forgery/anti-forgery-field)
     [:div#wrapper
      content]]))

(defn test-page []
  (html5
      [:head
        (include-css "/resources/jasmine/jasmine.css")
        (include-js "/resources/jasmine/jasmine.js"
                    "/resources/jasmine/jasmine-html.js"
                    "/resources/codemirror/codemirror.js"
                    "/resources/js/jquery-1.9.0.min.js")
        (include-cljs "shegon.test.frontend")
        [:script "
          (function() {
            var jasmineEnv = jasmine.getEnv();
            jasmineEnv.updateInterval = 1000;

            var htmlReporter = new jasmine.HtmlReporter();

            jasmineEnv.addReporter(htmlReporter);

            jasmineEnv.specFilter = function(spec) {
              return htmlReporter.specFilter(spec);
            };

            var currentWindowOnload = window.onload;

            window.onload = function() {
              if (currentWindowOnload) {
                currentWindowOnload();
              }
              execJasmine();
            };

            function execJasmine() {
              jasmineEnv.execute();
            }

          })();"]
      ]
      [:body ""]


    ))


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
  (GET "/" [] (layout
                [:div.repl "Loading... May take time if you've just ran it"]
                ;[:div.repl "Loading... May take time if you've just ran it"]
                ))

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

  (route/files "/_compiled" {
    :root @shegon.namespaces/public-output-path
  })

  (route/resources "/resources/")

  (GET "/tests" [] (test-page)))


(def server
  (-> (handler/site app-routes)
      ring.middleware.anti-forgery/wrap-anti-forgery
      (ring.middleware.session/wrap-session
        {:store (ring.middleware.session.cookie/cookie-store)})
      ring.middleware.params/wrap-params))


(defn run-if-not-running
  ([] (run-if-not-running server))
  ([handler]
    (when (nil? @running-server)
      (println "Running internal shegon CLJS server at http://127.0.0.1:19000/ ...")
      (println "If it's the first compilation in this process, please wait...")
      (reset! running-server
        (ring.adapter.jetty/run-jetty handler {
          :port   19000
          :join?  false
        })))
    @running-server))

(defn -main []
  (println "Joining the internal server")
  (.join (-> server
             (ring.middleware.reload/wrap-reload {:dirs ["src" "test"]})
             run-if-not-running)))
