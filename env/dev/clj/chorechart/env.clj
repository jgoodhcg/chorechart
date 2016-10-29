(ns chorechart.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [chorechart.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[chorechart started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[chorechart has shut down successfully]=-"))
   :middleware wrap-dev})
