(ns chorechart.pages.chart.page
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [cljs.pprint :refer [pprint]]
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
        (comp/filter-btn filter :week   "week")
        (comp/filter-btn filter :month  "month")
        (comp/filter-btn filter :custom "range")]]]

     (if (= filter :custom)
       [:div [:br] [:div.row
         [:div {:style {:text-align "center"}}
          [:div.form-group.col-xs-6
           [:input.form-control
            {:type "date"
             :on-change
             (fn [e]
               (rf/dispatch
                [:set-chart-filter-interval-start
                 (-> e .-target .-value)]))}]]
          [:div.form-group.col-xs-6
           [:input.form-control
            {:type "date"
             :on-change
             (fn [e] (rf/dispatch
                      [:set-chart-filter-interval-end
                       (-> e .-target .-value)]))}]]]]])

     (comp/chart-table chart)

     (if new-account
       (comp/new-account-link)
       (comp/chart-input chores))]))
