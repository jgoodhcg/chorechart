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
  (let [d (if (string? date)
            (clojure.string/replace date #"-" "/") ;; sql needs "-" but js/Date does wierd time zone stuff unless the string uses "/"
            date)]
    (new js/Date (date-string (new js/Date d)))))

(defn valid-interval
"taking two date obj, or strings, will return boolean if it is a valid interval"
  [start end]
  (let [s (zero-in-day start)
        e (zero-in-day end)]
    (< (.valueOf s)
       (.valueOf e))))

(defn start-of-week
  "takes any valid js/Date constructor arguments and returns a date obj of the monday of that week"
  [date]
  (let [d (new js/Date date)
        day_of_month (.getDate d)
        day_of_week (.getDay d)] ;; set up
    (.setDate d (+ (- day_of_month day_of_week) ;; sets d to the monday instead of sunday
                   (if (= day_of_week 0) -6 0)))
    d))

(defn end-of-week
  "takes any valid js/Date constructor arguments and returns a date obj of the monday of that week"
  [date]
  (let [d (new js/Date date)
        day_of_month (.getDate d)
        day_of_week (.getDay d)] ;; set up
    (.setDate d (-> day_of_month
                    (- day_of_week)
                    (- 1)
                    (+ 7))) ;; end of the week is Sunday
    d))

(defn start-of-month
  "takes any valid js/Date constructor arguments and returns a date object of the monday of that week"
  [date]
  (let [d (new js/Date date)]
    (.setDate d 1)
    d))

(defn end-of-month
  [date]
  (let [d (new js/Date date)
        year (.getFullYear d)
        next-month (+ 1 (.getMonth d))
        day 0]
    (new js/Date year next-month day)))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))
