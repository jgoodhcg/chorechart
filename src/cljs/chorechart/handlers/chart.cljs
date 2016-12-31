(ns chorechart.handlers.chart
  (:require [chorechart.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.async-flow-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [cljs.pprint :refer [pprint]]
            [re-frame.core :refer
             [dispatch reg-event-db reg-event-fx path]]))

(defn get-chart [_world [_ _]]
  (let [today (new js/Date)
        date (case (get-in _world [:db :chart-filter])
               :week  (misc/start-of-week today)
               :month (misc/start-of-month today)
               (misc/start-of-week today))] ;; last is default case option
    {:http-xhrio
     {:method          :post
      :uri             "/chart/view"
      :params          {:household_id
                        (get-in _world [:db :selected-household :household_id])
                        :date date}
      :timeout         5000
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [:set-chart]
      :on-failure      [:post-resp]}}))

(defn remove-chart-entry [_world [_ chart_id]]
  {:http-xhrio
   {:method          :post
    :uri             "/chart/remove"
    :params          {:chart_id chart_id}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format
                      {:keywords? true})
    :on-success      [:confirmed-remove-chart-entry]
    :on-failure      [:post-resp]}})

(defn send-chart-entry [_world [_ _]]
  (let [pending-chart-entry (get-in _world [:db :pending-chart-entry])]
    (if (every? #(contains? pending-chart-entry %)
                [:chore_id :moment :living_situation_id])
      {:http-xhrio
       {:method          :post
        :uri             "/chart/add"
        :params          (get-in _world [:db :pending-chart-entry])
        :timeout         5000
        :format          (ajax/json-request-format)
        :response-format (ajax/json-response-format {:keywords? true})
        :on-success      [:confirmed-chart-entry]
        :on-failure      [:post-resp]}}
      {:db (:db _world)})))

(defn confirmed-chart-entry [_world [_ living_situation_id]]
  {:db (assoc (:db _world) :pending-chart-entry {})
   :dispatch [:get-chart]})

(defn confirmed-remove-chart-entry [db [_ chart_entry_rm]]
  (let [chart (:chart db)
        chart_id_to_rm (:chart_id (first chart_entry_rm))]

    (assoc db :chart (filter #(not (= chart_id_to_rm
                                      (get % :chart_id)))
                             chart))))

(defn set-pending-chart-entry-chore-id [db [_ chore_id]]
  (assoc-in db [:pending-chart-entry :chore_id] chore_id))

(defn set-pending-chart-entry-date [db [_ date]]
  (assoc-in db [:pending-chart-entry :moment] date))

(defn set-pending-chart-entry-living-situation [db [_ _]]
  (assoc-in db [:pending-chart-entry :living_situation_id]
            (get-in db [:selected-household :living_situation_id])))

(defn set-chart [db [_ chart]]
  (assoc db :chart chart))

(defn set-chart-filter [db [_ filter]]
  (assoc db :chart-filter filter))

(defn set-chart-filter-interval-start [db [_ date]]
  (let [input (misc/zero-in-day date)
        end   (misc/zero-in-day (:chart-filter-interval-end db))]
    (if (< (.valueOf input)
           (.valueOf end))
      (assoc db :chart-filter-interval-start date)
      ;; if it isn't a valid date then set the start to be one day
      ;; behind the current end (setDate wraps for <1 and >28/29/30/31)
      (assoc db :chart-filter-interval-start
             (misc/date-string
              (new js/Date (.setDate end (- (.getDate end) 1))))))))

(defn set-chart-filter-interval-end [db [_ date]]
  (let [input (misc/zero-in-day date)
        start (misc/zero-in-day (:chart-filter-interval-start db))]
    (if (> (.valueOf input)
           (.valueOf start))
      (assoc db :chart-filter-interval-end date)
      ;; if it isn't a valid date then set the end to be one day
      ;; beyond the current start (setDate wraps for <1 and >28/29/30/31)
      (assoc db :chart-filter-interval-end
             (misc/date-string
              (new js/Date (.setDate start (+ (.getDate start) 1))))))))

(reg-event-fx :get-chart get-chart)
(reg-event-fx :remove-chart-entry remove-chart-entry)
(reg-event-fx :send-chart-entry send-chart-entry)
(reg-event-fx :confirmed-chart-entry confirmed-chart-entry)

(reg-event-db :confirmed-remove-chart-entry confirmed-remove-chart-entry)
(reg-event-db :set-pending-chart-entry-chore-id set-pending-chart-entry-chore-id)
(reg-event-db :set-pending-chart-entry-date set-pending-chart-entry-date)
(reg-event-db :set-pending-chart-entry-living-situation set-pending-chart-entry-living-situation)
(reg-event-db :set-chart set-chart)
(reg-event-db :set-chart-filter set-chart-filter)
(reg-event-db :set-chart-filter-interval-start set-chart-filter-interval-start)
(reg-event-db :set-chart-filter-interval-end set-chart-filter-interval-end)
