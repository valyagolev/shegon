(ns shegon.macros)

(defmacro example [& sts]
  `(shegon.examples/add-example
    ~(apply pr-str sts)
    (fn [] ~@sts)))


