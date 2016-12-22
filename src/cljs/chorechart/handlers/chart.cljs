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
  {:http-xhrio
   {:method          :post
    :uri             "/view/chart"
    :params          {:household_id
                      (get-in _world [:db :selected-household :household_id])
                      :date (misc/start-of-week (new js/Date))}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:set-chart]
    :on-failure      [:post-resp]}})

(defn remove-chart-entry [_world [_ chart_id]]
  {:http-xhrio
   {:method          :post
    :uri             "/chart/entry/remove"
    :params          {:chart_id chart_id}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format
                      {:keywords? true})
    :on-success      [:confirmed-remove-chart-entry]
    :on-failure      [:post-resp]}})

(defn send-chart-entry [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/chart/entry"
    :params          (get-in _world [:db :pending-chart-entry])
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:confirmed-chart-entry]
    :on-failure      [:post-resp]}})

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

(reg-event-fx :get-chart get-chart)
(reg-event-fx :remove-chart-entry remove-chart-entry)
(reg-event-fx :send-chart-entry send-chart-entry)
(reg-event-fx :confirmed-chart-entry confirmed-chart-entry)

(reg-event-db :confirmed-remove-chart-entry confirmed-remove-chart-entry)
(reg-event-db :set-pending-chart-entry-chore-id set-pending-chart-entry-chore-id)
(reg-event-db :set-pending-chart-entry-date set-pending-chart-entry-date)
(reg-event-db :set-pending-chart-entry-living-situation set-pending-chart-entry-living-situation)
(reg-event-db :set-chart set-chart)
