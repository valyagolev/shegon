(ns shegon.test.macros
  (:require [jayq.macros]))


(defn convec [colls]
  (vec (apply concat colls)))


(defmacro describe
  "Form:

  (describe \"something\"
    :let b1 (...)                 ; bound before each test
    :let b2 (...)
    :before (...)                 ; run before each test
    :before (...)
    \"can do some stuff\" (...)   ; test themselves
    \"can do other stuff\" (...)
    :after (...)                  ; run after each test
    :after (...))

  All bindings re-run before every test, so you can safely delete them
  in :after if these are elements, for example."

  [what & body]
  (let [clauses (map (fn [[[clause] body]] [clause body])
                  (partition 2 (partition-by keyword? body)))
        bindings-with-atoms
                (for [[clause [name value]] clauses
                      :when (= clause :let)] [name (gensym name) value])
        atom-bindings (convec (for [[_ an _] bindings-with-atoms] `(~an (atom nil))))
        let-bindings (convec (for [[n an _] bindings-with-atoms]
                                          `(~n @~an)))
        wrap-let #(list `fn [] `(let ~let-bindings ~@%))
        actual-clauses (concat
                          (map #(vec [:before `((reset! ~(second %) ~(nth % 2)))]) bindings-with-atoms)
                          (filter #(not= (first %) :let) clauses))]

    `(js/describe ~what
      (fn []
        (let ~atom-bindings
          ~@(for [[clause body] actual-clauses]
              (case clause
                :before `(js/beforeEach ~(wrap-let body))
                :after  `(js/afterEach ~(wrap-let body))
                :it     `(js/it ~(first body) ~(wrap-let (rest body)))
                :xit    `(js/xit ~(first body) ~(wrap-let (rest body))))))))))


(defmacro expect
  ([what] `(expect ~what true))
  ([what tobe] `(.toBe (js/expect ~what) ~tobe)))


(defmacro async-test
  "Form:
  (async 200
    [a (some-deferred...)
     b (ajax-or-something...)]
    (expect a 3)
    (expect b 4))"
  [ms bindings & body]
  (let [bindings-with-atoms (for [[n v] (partition 2 bindings)] [n (gensym n) v])
        atom-bindings (convec (for [[_ an _] bindings-with-atoms] `(~an (atom nil))))
        done (gensym "done")
        let-bindings (convec (for [[n an _] bindings-with-atoms]
                                          `(~n @~an)))]

    `(let ~(into atom-bindings `[~done (atom false)])
      (js/runs (fn []
        (jayq.macros/let-deferred ~bindings
          ~@(for [[n an _] bindings-with-atoms] `(reset! ~an ~n))
          (reset! ~done true))))
      (js/waitsFor (fn [] @~done) "The deffereds should resolve" ~ms)
      (js/runs (fn [] (let ~let-bindings ~@body))))))
