(ns chorechart.db)

(def default-db
  {:page :chart
   :households []
   :selected-household {}
   :chart []
   :chores []
   :pending-chart-entry {}
   })

;; filters for entries
;; :chore_name, :user_name, :moment

;; households drop down on chore entry view (only allowed to see one households chores at a time)

;; chore master list view seperate from entries ()
