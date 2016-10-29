(ns chorechart.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[chorechart started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[chorechart has shut down successfully]=-"))
   :middleware identity})
