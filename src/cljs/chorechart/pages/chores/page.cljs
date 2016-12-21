(ns chorechart.pages.chores.page
  (:require [re-frame.core :as rf]
            [chorechart.pages.chores.components :as comp]
            [chorechart.pages.misc-comps.list :refer [generic-list]]
            [chorechart.pages.misc-comps.add-new :refer [generic-add-new]]))

(defn chores-page []
  (rf/dispatch [:get-chores])
  (let [chores (rf/subscribe [:chores])]
    [:div.container
     [:div.row
      [:br]
      [:div.col-xs-12
       [:div
        (if (> (count @chores) 0)
          (generic-list @chores comp/chore-row)
          "no chores yet ):")]]]
     [:br]
     (generic-add-new
      "new chore name"
      :set-pending-add-chore
      :add-chore
      "add new chore")]))
