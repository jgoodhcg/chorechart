(ns chorechart.misc)

(defn date-string
  "creates a string in yyyy-mm-dd format from a js date obj"
  [date]
  (str (.getFullYear date) "-"
       (+ 1 (.getMonth date)) "-"
       (.getDate date)))

(defn zero-in-day
  "taking a date obj, or string, will return a new date object with Hours, Minutes, Seconds, and Milliseconds set to default 0"
  [date]
  (new js/Date (date-string (new js/Date date))))

(defn start-of-week
  "takes any valid js/Date constructor arguments and returns a string yyyy-mm-dd of the monday of that week"
  [date]
  (let [d (new js/Date date)
        day_of_month (.getDate d)
        day_of_week (.getDay d)] ;; set up
    (do
      (.setDate d (+ (- day_of_month day_of_week) ;; sets d to the monday
                     (if (= day_of_week 0) -6 1)))
      (date-string d))))

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
