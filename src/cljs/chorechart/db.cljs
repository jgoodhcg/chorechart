(ns chorechart.db
  (:require [chorechart.misc :as misc]))


(def default-db
  {:page :chart
   :households []
   :selected-household {}
   :chart []
   :chores []
   :pending-chart-entry {}
   :new-account false
   :chart-filter :week
   :chart-filter-interval-start (misc/date-string
                                 (let [d (new js/Date)]
                                   (.setDate d (- (.getDate d) 1))
                                   d))
   :chart-filter-interval-end (misc/date-string (new js/Date))
   :add-roomate-failed false
   :add-household-failed false
   })

;; filters for entries
;; :chore_name, :user_name, :moment

;; households drop down on chore entry view (only allowed to see one households chores at a time)

;; chore master list view seperate from entries ()
