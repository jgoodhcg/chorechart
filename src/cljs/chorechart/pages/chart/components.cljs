(ns chorechart.pages.chart.components
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [chorechart.pages.misc-comps.select :refer [select]]))

(defn filter-btn [this-filter btn-filter btn-value]
  [(if (= this-filter btn-filter)
     :button.btn.btn-sm.btn-primary
     :button.btn.btn-sm.btn-secondary)
   {:on-click (fn [_] (rf/dispatch [:set-chart-filter btn-filter]))}
   btn-value])

(defn custom-interval-input [valid-interval]
  [:div [:br]
   [:div.row
    [:div {:style {:text-align "center"}}
     [(keyword
       (str "div.form-group.col-xs-6"
            (if-not valid-interval ".has-danger" "")))
      [:input.form-control
       {:type "date"
        :on-change
        (fn [e]
          (rf/dispatch
           [:set-chart-filter-interval-start
            (-> e .-target .-value)]))}]]
     [(keyword
       (str "div.form-group.col-xs-6"
            (if-not valid-interval ".has-danger" "")))
      [:input.form-control
       {:type "date"
        :on-change
        (fn [e]
          (rf/dispatch
           [:set-chart-filter-interval-end
            (-> e .-target .-value)])
          (rf/dispatch [:get-chart]))}]]]]])

(defn chart-table [chart]
  [:div.row
   [:div.col-xs-12
    [:table.table {:style {:overflow "scroll" :margin-bottom "20em"}}
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
         [:div
          [:div.row
           [:div.col-xs-9.form-group
            [:h3 [:span.tag.tag-primary household_name]]]
           [:div {:style {:position "absolute" :right "0.5em"}}
            [:input.btn.btn-sm {:type "button" :value "▼"
                                :on-click #(reset! collapsed true)}]]]
          [:div.row
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
              :placeholder "mm/dd/yyyy"
              :on-change
              #(rf/dispatch
                [:set-pending-chart-entry-date (-> % .-target .-value)])}]]
           [:div.col-xs-12.col-sm-4.form-group
            [:input.btn.btn-primary.btn-block
             {:type "button" :value "submit" :width "100%"
              :on-click #(rf/dispatch [:send-chart-entry])}]]]]

         ;; collapsed
         [:div.row
          [:div.col-xs-9.form-group
           [:h3 [:span.tag.tag-default household_name]]]
          [:div {:style {:position "absolute" :right "0.5em"}}
           [:input.btn.btn-sm {:type "button" :value "▼"
                               :on-click #(reset! collapsed false)}]]])])))

(defn new-account-link []
  [:a.btn.btn-primary.btn-block
   {:href "#/info"
    :style
    {:box-shadow
     " 0 19px 38px rgba(0,0,0,0.30), 0 15px 12px"}}
   "Get Started"])
