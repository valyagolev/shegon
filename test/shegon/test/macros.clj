(ns shegon.test.macros)


(defmacro describe
  "Form:

  (describe \"something\"
    [b1 (...) b2 (...)]           ; bound before each test
    :before (...)                 ; run before each test
    :before (...)
    \"can do some stuff\" (...)   ; test themselves
    \"can do other stuff\" (...)
    :after (...)                  ; run after each test
    :after (...))

  All bindings re-run before every test, so you can safely delete them
  in :after if these are elements, for example."

  [what bindings & body]
  (let [bindings (partition 2 bindings)
        binding-names (map first bindings)
        atom-names (map gensym binding-names)
        clauses (concat
                  (map (fn [an [_ bv]]
                              [:before `(reset! ~an ~bv)])
                            atom-names bindings)
                  (partition 2 body))
        let-bindings (vec (apply concat
                        (map (fn [bn an] `(~bn @~an))
                                  binding-names atom-names)))
        wrap-let (fn [body] `(fn [] (let ~let-bindings ~body)))]
    `(js/describe ~what
      (fn []
        (let ~(vec (apply concat (for [an atom-names] `(~an (atom)))))
          ~@(for [[clause body] clauses]
              (case clause
                :before `(js/beforeEach ~(wrap-let body))
                :after  `(js/afterEach ~(wrap-let body))
                `(js/it ~clause ~(wrap-let body)))))))))


(defmacro expect
  ([what] `(expect ~what true))
  ([what tobe] `(.toBe (js/expect ~what) ~tobe)))
