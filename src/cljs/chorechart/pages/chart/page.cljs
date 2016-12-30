(ns chorechart.pages.chart.page
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [chorechart.pages.chart.components :as comp]))

(defn chart-page []
  (rf/dispatch [:get-chart])
  (rf/dispatch [:set-pending-chart-entry-living-situation])
  (let [chart @(rf/subscribe [:chart])
        chores @(rf/subscribe [:chores])
        new-account @(rf/subscribe [:new-account])
        filter @(rf/subscribe [:chart-filter])]

    [:div.container-fluid
     [:div.row
      [:div.col-xs-12 {:style {:text-align "center"}}
       [:div.btn-group
        (comp/filter-btn filter :this-week  "this week")
        (comp/filter-btn filter :last-week  "last week")
        (comp/filter-btn filter :this-month "this month")
        (comp/filter-btn filter :last-month "last month")]]]

     (comp/chart-table chart)

     (if new-account
       (comp/new-account-link)
       (comp/chart-input chores))]))
