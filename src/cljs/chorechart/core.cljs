(ns chorechart.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [chorechart.handlers]
            [chorechart.subscriptions]

            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [ajax.core :refer [GET POST]]
            [chorechart.ajax :refer [load-interceptors!]]

            [chorechart.pages.chart.page :refer [chart-page]]
            [chorechart.pages.households.page :refer [households-page]]
            [chorechart.pages.roomates.page :refer [roomates-page]]
            [chorechart.pages.chores.page :refer [chores-page]]

            ;; eventually get rid of requires below this line
            [chorechart.pages.misc-comps.row-cases :refer [row-case-options row-case-edit]]
            [cljs.pprint :refer [pprint]]
            [chorechart.misc :as misc]
            [chorechart.pages.misc-comps.add-new :refer [generic-add-new]]
            [chorechart.pages.misc-comps.list :refer [generic-list]]
            )
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
    (let [selected-page (rf/subscribe [:page])]
      [:nav.navbar.navbar-light.bg-faded {:style {:border-radius "0em"}}
       [:div.row
        [:div.col-xs-6
         [:a.navbar-brand {:href "#/"} "Chorechart"]]
        [:div.col-xs-4
         [:ul.nav.navbar-nav.hidden-md-up
          [:li.nav-item.active
           [:a.nav-link {:href (str "#/" (name @selected-page))} @selected-page]]]]
        [:div.col-xs-2
         [:input.hidden-md-up.btn.btn-sm
          {:type "button" :value "☰" :on-click #(swap! collapsed? not) }]]]
       [:div.row
        [:div.col-xs-12
         [:div.collapse.navbar-toggleable-sm
          (when-not @collapsed? {:class "in"})
          [:ul.nav.navbar-nav
           [nav-link "#/chart" "Chart" :chart collapsed?]
           [nav-link "#/households" "Households" :households collapsed?]
           [nav-link "#/roomates" "Roomates" :roomates collapsed?]
           [nav-link "#/chores" "Chores" :chores collapsed?]
           [nav-link "#/info" "Info" :info collapsed?]
           [:li.nav-item
            [:a.nav-link
             {:href "/logout"} "Logout"]]]]]]])))

(defn row [label input]
  [:div.row
   [:div.col-xs-2 [:label label]]
   [:div.col-xs-5 input]])

(defn input [label name type dispatch-key]
  (row label [:input.form-control {:type type
                                   :name name
                                   :on-change
                                   #(rf/dispatch [dispatch-key (-> % .-target .-value)])}]))

(defn get-btn [dispatch-key]
  [:button.btn.btn-primary
   {:on-click
      #(do
        (pprint (str "dispatched " dispatch-key))
        (rf/dispatch [dispatch-key]))}
     (str dispatch-key)])

(defn print-btn []
  [:button.btn
   {:on-click
    #(do
       (pprint "print pressed")
       (rf/dispatch [:print-db]))}
   "print"])

(defn debug-page []
    [:div.container [:br]
     (str "email: " js/email) [:br]
     (prn-str "person " js/person) [:br]
     [:div
      (get-btn :get-households) [:br]
      (get-btn :get-chart) [:br]
      (get-btn :get-chores) [:br]
      (print-btn)]])

(defn info-page []
  [:div.container [:br]
   [:div.row
    [:div.col-xs-12
     [:h1 "Getting Started"]
     [:hr]
     [:ol
      [:li [:p "add a  "
            [:a.btn.btn-primary.btn-sm {:href "#/households"} "household"]]]
      [:li [:p "add chores for your selected household  "
            [:a.btn.btn-primary.btn-sm {:href "#/chores"} "add chores"]]]
      [:li
       [:p
        "invite roomates to your selected household via email (roomates must be signed up)  "
            [:a.btn.btn-primary.btn-sm {:href "#/roomates"} "invite roomates"]]]
      [:li [:p "add entries to your chart  "
            [:a.btn.btn-primary.btn-sm {:href "#/chart"} "add to chart"]]]]]]])

(def pages
  {:debug #'debug-page
   :info #'info-page
   :chart #'chart-page
   :households #'households-page
   :chores #'chores-page
   :roomates #'roomates-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :chart]))

(secretary/defroute "/households" []
  (rf/dispatch [:set-active-page :households]))

(secretary/defroute "/chart" []
  (rf/dispatch [:set-active-page :chart]))

(secretary/defroute "/roomates" []
  (rf/dispatch [:set-active-page :roomates]))

(secretary/defroute "/chores" []
  (rf/dispatch [:set-active-page :chores]))

(secretary/defroute "/debug" []
  (rf/dispatch [:set-active-page :debug]))

(secretary/defroute "/info" []
  (rf/dispatch [:set-active-page :info]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:boot])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components)
  )
