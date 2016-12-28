(ns chorechart.handlers.roomates
  (:require [chorechart.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.async-flow-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [cljs.pprint :refer [pprint]]
            [re-frame.core :refer
             [dispatch reg-event-db reg-event-fx path]]))

(defn add-roomates [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/roomates/add"
    :params          (get-in _world [:db :pending-add-roomate])
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:confirmed-add-roomate]
    :on-failure      [:post-resp]}})

(defn get-roomates-selected-household [_world [_ living_situation_id]]
  {:http-xhrio
   {:method          :post
    :uri             "/roomates/view"
    :params          (get-in _world [:db :selected-household])
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:set-roomates-selected-household]
    :on-failure      [:post-resp]}})

(defn confirmed-add-roomate [db [_ new_roomate]]
  (-> db
      (assoc :pending-add-roomate {})
      (assoc-in [:selected-household :roomates] new_roomate)))

(defn set-pending-roomate [db [_ roomate_email]]
  (assoc db :pending-add-roomate
         {:roomate_email roomate_email
          :living_situation_id
          (get-in db [:selected-household :living_situation_id])}))

(defn set-roomates-selected-household [db [_ roomates]]
  (if (empty? roomates)
    db
    (assoc-in db [:selected-household :roomates] roomates)))

(reg-event-fx :add-roomate add-roomates)
(reg-event-fx :get-roomates-selected-household get-roomates-selected-household)

(reg-event-db :confirmed-add-roomate confirmed-add-roomate)
(reg-event-db :set-pending-roomate set-pending-roomate)
(reg-event-db :set-roomates-selected-household set-roomates-selected-household)
