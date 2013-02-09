(ns shegon.test
  (:use clojure.test
        shegon.compiler))

(deftest compiles-javascript
  (is (= (compile-js {:source "(+ x 5)"})
         '{:result "(shegon.user.x + 5)", :ns shegon.user}))

  (is (= (compile-js {:source "(def a (+ b 5))" :ns 'hello})
         '{:result "hello.a = (hello.b + 5)", :ns hello}))

  (is (= (compile-js {:source "(ns change-ns)"})
         '{:result "goog.provide('change_ns');\ngoog.require('cljs.core');\n", :ns change-ns})))


