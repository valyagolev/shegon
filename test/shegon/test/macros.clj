(ns shegon.test.macros)


(defmacro describe [what bindings & body]
  `(js/describe ~what
    (fn []
      (let ~bindings
        ~@(for [[clause body] (partition 2 body)]
            (case clause
              :before `(js/beforeEach (fn [] ~body))
              :after  `(js/afterEach (fn [] ~body))
              `(js/it ~clause (fn [] ~body))))))))


(defmacro expect
  ([what] `(expect ~what true))
  ([what tobe] `(.toBe (js/expect ~what) ~tobe)))
