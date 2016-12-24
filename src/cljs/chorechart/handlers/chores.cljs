(ns chorechart.handlers.chores
  (:require [chorechart.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.async-flow-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [cljs.pprint :refer [pprint]]
            [re-frame.core :refer
             [dispatch reg-event-db reg-event-fx path]]))

(defn get-chores [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/view/chores"
    :params          {:household_id
                      (get-in _world
                              [:db :selected-household :household_id])}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format
                      {:keywords? true})
    :on-success      [:set-chores]
    :on-failure      [:post-resp]}})

(defn remove-chore [_world [_ chore_id]]
  {:http-xhrio
   {:method          :post
    :uri             "/remove/chore"
    :params          {:chore_id chore_id}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format
                      {:keywords? true})
    :on-success      [:confirmed-remove-chore]
    :on-failure      [:post-resp]}})

(defn edit-chore [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/edit/chore"
    :params          (get-in _world
                             [:db :pending-edit-chore ])
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format
                      {:keywords? true})
    :on-success      [:confirmed-edit-chore]
    :on-failure      [:post-resp]}})

(defn add-chore [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/add/chore"
    :params          {:chore_name
                      (:chore_name (get-in _world [:db :pending-add-chore]))
                      :household_id (get-in _world [:db :selected-household :household_id])}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format
                      {:keywords? true})
    :on-success      [:confirmed-add-chore]
    :on-failure      [:post-resp]}})

(defn set-pending-add-chore [db [_ chore_name]]
  (assoc db :pending-add-chore {:chore_name chore_name}))

(defn set-pending-edit-chore [db [_ chore]]
  (assoc db :pending-edit-chore chore))

(defn confirmed-add-chore[db [_ added_chore]]
  (assoc db :chores (conj (:chores db) added_chore)))

(defn confirmed-edit-chore [db [_ edited_chore]]
  (pprint "edited chore")
  (pprint edited_chore)
  db)

(defn confirmed-remove-chore [db [a chore_gone]]
  db
  ;; (assoc db :chores
  ;;        (vec (filter #(not (= (:chore_id chore_gone)
  ;;                                (get % :id)))
  ;;                     (:chores db))))
)

(reg-event-fx :get-chores get-chores)
(reg-event-fx :edit-chore edit-chore)
(reg-event-fx :remove-chore remove-chore)
(reg-event-fx :add-chore add-chore)

(reg-event-db :set-pending-add-chore set-pending-add-chore )
(reg-event-db :set-pending-edit-chore set-pending-edit-chore)
(reg-event-db :confirmed-add-chore confirmed-add-chore)
(reg-event-db :confirmed-remove-chore confirmed-remove-chore)
(reg-event-db :confirmed-edit-chore confirmed-edit-chore)
