(ns user
  (:require [mount.core :as mount]
            [chorechart.figwheel :refer [start-fw stop-fw cljs]]
            chorechart.core))

(defn start []
  (mount/start-without #'chorechart.core/repl-server))

(defn stop []
  (mount/stop-except #'chorechart.core/repl-server))

(defn restart []
  (stop)
  (start))


