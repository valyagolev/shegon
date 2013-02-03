(ns shegon.history)

(def -history-log (atom ()))
(def -history-pos (atom -1))
(def -history-current (atom ""))

(def -local-storage-history-limit 100)

(defn accurate-inc [val max]
  (if (< val max)
      (inc val)
      val))

(defn accurate-dec [val min]
  (if (> val min)
      (dec val)
      val))

(defn history-el [i]
  (if (= i -1)
      @-history-current
      (nth @-history-log i)))


(defn add-input [input]
  (swap! -history-log conj input))

(defn reset-pos []
  (reset! -history-current -1))

(defn move [current direction]
  (when (= @-history-pos -1)
    (reset! -history-current current))

  (if (= direction :up)
    (swap! -history-pos accurate-inc (dec (count @-history-log)))
    (swap! -history-pos accurate-dec -1))

  (history-el @-history-pos))

(defn commit-to-local-storage [_ _ _ ns]
  (set! window.localStorage/repl-history
    (JSON/stringify
      (clj->js
        (take -local-storage-history-limit ns)))))

(when window.localStorage/repl-history
  (reset! -history-log
    (seq
      (js->clj
        (JSON/parse window.localStorage/repl-history)))))

(add-watch shegon.history/-history-log :localStorage commit-to-local-storage)

