(ns chorechart.pages.chart.components
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [chorechart.pages.misc-comps.select :refer [select]]))

(defn chart-table [chart]
  [:div.row
   [:div.col-xs-12
    [:table.table
     [:thead
      [:tr
       [:th "Person"]
       [:th "Chore"]
       [:th "Date"]
       [:th "Delete"]]]
     [:tbody
      (map
       #(vec [:tr {:key (.indexOf chart %)}
              [:td (:user_name %)]
              [:td (:chore_name %)]
              [:td (subs (:moment %) 5 10)]
              [:td (let [chart_id (:chart_id %)]
                     [:input.btn.btn-sm.btn-secondary
                      {:type "button"
                       :value "X"
                       :on-click
                       (fn [e]
                         (rf/dispatch
                          [:remove-chart-entry
                           chart_id]))}])]])
       chart)]]]])

(defn chart-input [chores]
  (r/with-let [collapsed (r/atom false)]
    (let [user_name @(rf/subscribe [:user_name])
          household_name (:house_name @(rf/subscribe [:selected-household]))]
      [:div.container-fluid.bg-faded {:style { :width "100%" :padding-top "1em"
                                              :position "fixed" :bottom "0em"
                                              :left "0em" :right "0em"}}
       (if-not @collapsed
         ;; expanded
         [:div.row
          [:div.row
           [:div.col-xs-9.offset-xs-1.form-group
            [:h3 [:span.tag.tag-default household_name]]]
           [:div.col-xs-1.form-group
            [:input.btn.btn-sm {:type "button" :value "▼"
                                :on-click #(reset! collapsed true)}]]]
          [:div.row
           [:div.col-xs-12-down.col-sm-4.form-group
            [:input.form-control {:type "text" :disabled true
                                  :style {:width "100%"} :value user_name}]]
           [:div.col-xs-12.col-sm-4.form-group
            (select "name" :set-pending-chart-entry-chore-id
                    (map
                     #(hash-map
                       :value (:id %)
                       :label (:chore_name %)) ;; format chore maps for select fn
                     chores))]
           [:div.col-xs-12.col-sm-4.form-group
            [:input.form-control
             {:type "date" :style {:width "100%"}
              :on-change
              #(rf/dispatch
                [:set-pending-chart-entry-date (-> % .-target .-value)])}]]
           [:div.col-xs-12.col-sm-12.form-group
            [:input.btn.btn-primary.btn-block
             {:type "button" :value "submit" :width "100%"
              :on-click #(rf/dispatch [:send-chart-entry])}]]]]

         ;; collapsed
         [:div.row
          [:div.col-xs-9.offset-xs-1.form-group
           [:h3 [:span.tag.tag-default household_name]]]
          [:div.col-xs-1.form-group
           [:input.btn.btn-sm {:type "button" :value "▼"
                               :on-click #(reset! collapsed false)}]]])])))

(defn new-account-link []
  [:a.btn.btn-primary.btn-block
   {:href "#/info"
    :style
    {:box-shadow
     " 0 19px 38px rgba(0,0,0,0.30), 0 15px 12px"}}
   "Get Started"])
