(ns chorechart.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [cljs.pprint :refer [pprint]]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [chorechart.ajax :refer [load-interceptors!]]
            [chorechart.handlers]
            [chorechart.subscriptions])
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
      [:nav.navbar.navbar-dark.bg-primary
       [:div.row
        [:div.col-xs-10
         [:a.navbar-brand {:href "#/"} "chorechart"]
         [:ul.nav.navbar-nav.hidden-md-up
          [:li.nav-item.active
           [:a.nav-link {:href (str "#/" (name @selected-page))} @selected-page]]]]
        [:div.col-xs-2
         [:button.navbar-toggler.hidden-md-up
          {:on-click #(swap! collapsed? not)} "☰"]]]
       [:div.row
        [:div.col-xs-12
         [:div.collapse.navbar-toggleable-sm
          (when-not @collapsed? {:class "in"})
          [:ul.nav.navbar-nav
           [nav-link "#/" "Home" :home collapsed?]
           [nav-link "#/chart" "chart" :chart collapsed?]
           [nav-link "#/households" "Households" :households collapsed?]
           ]]
         ]]
       ])))

(defn row [label input]
  [:div.row
   [:div.col-xs-2 [:label label]]
   [:div.col-xs-5 input]])

(defn input [label name type dispatch-key]
  (row label [:input.form-control {:type type
                                   :name name
                                   :on-change
                                   #(rf/dispatch [dispatch-key (-> % .-target .-value)])}]))

(defn select [label name dispatch-key options]
  [:div
   [:select {:name name
                   :defaultValue "default"
                   :style {:width "100%"}
                   :on-change #(rf/dispatch
                                [dispatch-key (-> % .-target .-value)])}
         (cons
          [:option {:value "default" :disabled true} "-- choose an option --"]
          (mapv #(-> [:option {:value (:value %) :key (str (.indexOf options %)) } (:label %)]) options))
    ]
   ])

(defn get-btn []
  [:button.btn
   {:on-click
      #(do
        (pprint "get pressed")
        (rf/dispatch [:get-all-user-state])
        )
      }
     "get"])

(defn print-btn []
  [:button.btn
   {:on-click
    #(do
       (pprint "print pressed")
       (rf/dispatch [:print-db])
       )
    }
   "print"])

(defn home-page []
  [:div.container
   (str js/user_name " home page")
   [:br]
   (get-btn)
   (print-btn)
   ])

(defn households-page []
  (let [households (rf/subscribe [:households])]
    (rf/dispatch [:get-households])
    [:div (str @households)])
  )

(defn chart-page []
  (let [subscribe "to something"]
    [:div.container-fluid
     [:div.row
      [:div.col-xs-12 {:style {:background-color "#787878"}}
       [:div.col-xs-4 "Person"] [:div.col-xs-4 "Chore"] [:div.col-xs-4 "Date"]
       ]]
     [:div.row {:style {:position "fixed" :bottom "10px"}}
      [:div.hidden-xs-down.col-sm-4
        [:input {:type "text" :disabled true :style {:width "100%"} :value "Name"}]]
      [:div.col-xs-5.col-sm-4
       (select "label" "name" :dispatch-event [{:value "option-2" :label "option-dos"}])]
      [:div.col-xs-5.col-sm-4
       [:input {:type "date" :style {:width "100%"}}]]
      [:div.col-xs-2
       [:input.button.button-default {:type "button"}]]
       ]]))

(def pages
  {:home #'home-page
   :chart #'chart-page
   :households #'households-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/households" []
  (rf/dispatch [:set-active-page :households]))

(secretary/defroute "/chart" []
  (rf/dispatch [:set-active-page :chart]))
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
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components)
  (rf/dispatch [:get-all-user-state])
  )
