(ns shegon.views
  (:require [clj-stacktrace.repl]
            [shegon.compiler])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page :only [include-css include-js html5]]))


(defpartial layout [& content]
            (html5
              [:head
               [:title "shegon"]
               (include-css "/codemirror/codemirror.css"
                            "/css/reset.css"
                            "/css/shegon.css")
               (include-js "/codemirror/codemirror.js"
                           "/codemirror/clojure.js"
                           "/codemirror/javascript.js"
                           "/codemirror/matchbrackets.js"
                           "/js/jquery-1.9.0.min.js"
                           "/js/shegon.js")]
              [:body
               [:div#wrapper
                content]]))


(defpartial compiler-form [{:keys [source]}]
    (let [source# (gensym "source")
          form# (gensym "form")]
       [:form {:class "compiler-form" :action "/compiler" :method :post}
        [:textarea {:id source# :name :source} source]
        [:input {:type :submit :value "Compile (or ⌘-Enter)"}]]))

(defpartial output [{:keys [exception result]}]
    (let [source# (gensym "source")]
      [:div
        [:textarea {:id source#} (or result (clj-stacktrace.repl/pst-str exception))]
        [:script "var textarea = document.getElementById('" source# "');
                  CodeMirror.fromTextArea(textarea, {'readOnly': true});"]]))

(defpage [:get "/repl"] []
  (layout
    (let [repl# (gensym "repl")]
      [:textarea {:id repl# :class "repl"}])))


(defpartial compiler-page [params]
  (layout
    (compiler-form params)
    (output (shegon.compiler/compile-js params))))

(defpage [:post "/compiler"] {:as params}
  (compiler-page params))

(defpage [:get "/compiler"] []
  (compiler-page {:source "(defn plus [a b] (+ a b))\n\n(js/alert (plus 19 19))"}))


(defpage [:get "/"] []
  (layout
    [:ul
      [:li [:a {:href "/compiler"} "Compiler"]]]))
