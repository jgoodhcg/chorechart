(ns chorechart.pages.roomates.page
  (:require [re-frame.core :as rf]
            [chorechart.pages.households.components :as comp]
            [chorechart.pages.misc-comps.list-no-options :refer [generic-list-no-options]]
            [chorechart.pages.misc-comps.add-new :refer [generic-add-new]]))

(defn roomates-page []
  (rf/dispatch [:get-roomates-selected-household])
  (let [selected_household (rf/subscribe [:selected-household])]
    [:div.container
     [:div.row
      [:br]
      [:div.col-xs-12
       [:div.list-group
        [:div.list-group-item.text-xs-center
         {:style {:background-color "#f4f4f5"}}
         [:h3 (:house_name @selected_household)]]]
       (generic-list-no-options (:roomates @selected_household) :user_name)
       [:br]
       (generic-add-new
        "user's email"
        :set-pending-roomate
        :add-roomate
        "add new person")]]]))

