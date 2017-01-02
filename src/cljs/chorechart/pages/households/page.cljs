(ns chorechart.pages.households.page
  (:require [re-frame.core :as rf]
            [chorechart.pages.households.components :as comp]
            [chorechart.pages.misc-comps.list :refer [generic-list]]
            [chorechart.pages.misc-comps.add-new :refer [generic-add-new]]))

(defn households-page []
  (rf/dispatch [:get-households])
  (let [households @(rf/subscribe [:households])
        add-household-failed @(rf/subscribe [:add-household-failed])]
    [:div.container
     [:div.row
      [:br]
      [:div.col-xs-12
       [:div
        (if (> (count households) 0)
          (generic-list households comp/household-row)
          "no households yet ):")]]]
     [:br]
     (generic-add-new
      "new houshold name"
      :set-pending-household
      :add-household
      "add new household"
      add-household-failed)]))
