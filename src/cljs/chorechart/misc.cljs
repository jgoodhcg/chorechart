(ns chorechart.misc)

(defn start-of-week
  "takes any valid js/Date constructor arguments and returns a string yyyy-mm-dd of the monday of that week"
  [date]
  (let [d (new js/Date date)
        day_of_month (.getDate d)
        day_of_week (.getDay d)] ;; set up
    (do
      (.setDate d (+ (- day_of_month day_of_week) ;; sets d to the monday
                     (if (= day_of_week 0) -6 1)))
      (str (.getFullYear d) "-" ;; creates a string in yyyy-mm-dd format
           (+ 1 (.getMonth d)) "-"
           (.getDate d)))))

(defn start-of-month
  "takes any valid js/Date constructor arguments and returns a string yyyy-mm-dd of the monday of that week"
  [date]
  (let [d (new js/Date date)]
    (do
      (.setDate d 1)
      (str (.getFullYear d) "-" ;; creates a string in yyyy-mm-dd format
           (+ 1 (.getMonth d)) "-"
           (.getDate d)))))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))
