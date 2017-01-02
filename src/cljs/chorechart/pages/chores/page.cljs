(ns chorechart.pages.chores.page
  (:require [re-frame.core :as rf]
            [chorechart.pages.chores.components :as comp]
            [chorechart.pages.misc-comps.list :refer [generic-list]]
            [chorechart.pages.misc-comps.add-new :refer [generic-add-new]]))

(defn chores-page []
  (rf/dispatch [:get-chores])
  (let [chores @(rf/subscribe [:chores])
        selected_household @(rf/subscribe [:selected-household])
        add-chore-failed @(rf/subscribe [:add-chore-failed])]
    [:div.container
     [:div.row
      [:br]
      [:div.col-xs-12
       [:div.list-group
        [:div.list-group-item.text-xs-center
         {:style {:background-color "#f4f4f5"}}
         [:h3 (:house_name selected_household)]]]
       [:div
        (if (> (count chores) 0)
          (generic-list chores comp/chore-row)
          "no chores yet ):")]]]
     [:br]
     (generic-add-new
      "new chore name"
      :set-pending-add-chore
      :add-chore
      "add new chore"
      add-chore-failed)]))
