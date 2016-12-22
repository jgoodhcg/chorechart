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
            [chorechart.pages.info.page :refer [info-page]]
            [chorechart.pages.misc-comps.nav :refer [navbar]]
            [chorechart.pages.debug.page :refer [debug-page]]

            ;; eventually get rid of requires below this line
            [chorechart.pages.misc-comps.row-cases :refer [row-case-options row-case-edit]]
            [cljs.pprint :refer [pprint]]
            [chorechart.misc :as misc]
            [chorechart.pages.misc-comps.add-new :refer [generic-add-new]]
            [chorechart.pages.misc-comps.list :refer [generic-list]])
  (:import goog.History))

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
