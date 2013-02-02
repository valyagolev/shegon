(ns shegon.views
  (:require [noir.content.getting-started]
            [clj-stacktrace.repl]
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
                           "/codemirror/matchbrackets.js")]
              [:body
               [:div#wrapper
                content]]))


(defpartial compiler-form [{:keys [source]}]
    (let [source# (gensym "source")
          form# (gensym "form")]
      [:div
       [:form {:id form# :action "/compiler" :method :post}
        [:textarea {:id source# :name :source} source]
        [:input {:type :submit :value "Compile (or ⌘-Enter)"}]]
       [:script "var form = document.getElementById('" form# '"');
                 var textarea = document.getElementById('" source# "');

                 var config = {'matchBrackets': true,
                               'extraKeys':
                                {'Cmd-Enter':
                                    function(){ form.submit(); }}};

                 CodeMirror.fromTextArea(textarea, config);"]]))

(defpartial output [{:keys [exception result]}]
    (let [source# (gensym "source")]
      [:div
        [:textarea {:id source#} (or result (clj-stacktrace.repl/pst-str exception))]
        [:script "var textarea = document.getElementById('" source# "');
                  CodeMirror.fromTextArea(textarea, {'readOnly': true});"]]))


(defpage [:get "/compiler"] []
         (layout
           (compiler-form {})))



(defpage [:post "/compiler"] {:as params}
         (layout
            (compiler-form params)
            (output (shegon.compiler/compile-js params))))
