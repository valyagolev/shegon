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

(defn history-el []
  (if (= @-history-pos -1)
      @-history-current
      (nth @-history-log @-history-pos)))


(defn add-input [input]
  (swap! -history-log conj input))

(defn changed-current [current]
  (when (or (not= (history-el) current))
    (reset! -history-current current)
    (reset! -history-pos -1)))

(defn move [direction]
  (case direction
    :up   (swap! -history-pos accurate-inc (dec (count @-history-log)))
    :down (swap! -history-pos accurate-dec -1))
  (history-el))

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

