(ns chorechart.pages.chart.page
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [chorechart.pages.chart.components :as comp]))

(defn chart-page []
  (rf/dispatch [:get-chart])
  (rf/dispatch [:set-pending-chart-entry-living-situation])
  (let [chart (rf/subscribe [:chart])
        chores (rf/subscribe [:chores])
        new-account (rf/subscribe [:new-account])]
    [:div.container-fluid
     (comp/chart-table @chart)
     (if @new-account
       (comp/new-account-link)
       (comp/chart-input @chores))]))
