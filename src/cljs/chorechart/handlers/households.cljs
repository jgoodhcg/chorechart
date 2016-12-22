(ns chorechart.handlers.households
  (:require [chorechart.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.async-flow-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [cljs.pprint :refer [pprint]]
            [re-frame.core :refer
             [dispatch reg-event-db reg-event-fx path]]))

(defn get-households [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/view/households"
    :params          {:person_id (get-in _world [:db :id])}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:set-households]
    :on-failure      [:post-resp]}})

(defn add-household [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/add/household"
    :params          {:house_name
                      (:house_name (get-in _world [:db :pending-add-household]))
                      :person_id (get-in _world [:db :id])}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:confirmed-add-household]
    :on-failure      [:post-resp]}})

(defn edit-household [_world [_ _]]
  {:http-xhrio
   {:method          :post
    :uri             "/edit/household"
    :params          (select-keys (get-in _world [:db :pending-edit-household])
                                  [:new_house_name :living_situation_id])
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:confirmed-edit-household]
    :on-failure      [:post-resp]}})

(defn remove-household [_world [_ living_situation_id]]
  {:http-xhrio
   {:method          :post
    :uri             "/remove/living-situation"
    :params          {:living_situation_id living_situation_id}
    :timeout         5000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [:confirmed-remove-household]
    :on-failure      [:post-resp]}})

(defn confirmed-add-household [_world [_ new_household]]
  (let [db (:db _world)]
    {:db (assoc db
                :new-account false
                :pending-add-household {}
                :households (conj (:households db) (first new_household)))
     :dispatch [:set-selected-household]}))

(defn set-pending-household [db [_ house_name]]
  (assoc db :pending-add-household {:house_name house_name}))

(defn set-pending-edit-household [db [_ info]]
  (assoc db :pending-edit-household
         (select-keys info [:new_house_name :living_situation_id])))

(defn confirmed-remove-household [db [_ household_gone]]
  (let [liv_sit_id_to_rm (:living_situation_id (first household_gone))
        selected_household (:selected-household db)
        selected_household_liv_sit_id (:living_situation_id selected_household)
        households (:households db)]
    (assoc db
           :households
           (vec (filter #(not (= liv_sit_id_to_rm
                                 (get % :living_situation_id)))
                        households))
           :selected-household
           (if (= selected_household_liv_sit_id liv_sit_id_to_rm)
             {}
             selected_household))))

(defn confirmed-edit-household [db [a b]]
  (pprint "confirmed edit household")
  db)

(defn set-households [db [_ households]]
  (assoc db :households households))

(defn set-selected-household [db [_ selected_living_situation_id]]
  (let [selected_household (:selected-household db)
        households (:households db)]
    (assoc db :selected-household
           (if selected_living_situation_id ;; if given a living situation
             (first                         ;; set that is the selected
              (filter
               #(= selected_living_situation_id (get % :living_situation_id))
               households))
             (if (or (nil? selected_household)    ;; set a default if there isn't
                     (empty? selected_household)) ;; if there isn't one
               (first households)
               selected_household)))))

(reg-event-fx :get-households get-households)
(reg-event-fx :add-household add-household)
(reg-event-fx :edit-household edit-household)
(reg-event-fx :remove-household remove-household)
(reg-event-fx :confirmed-add-household cconfirmed-add-household)

(reg-event-db :set-selected-household set-selected-household)
(reg-event-db :set-pending-household set-pending-household)
(reg-event-db :set-pending-edit-household set-pending-edit-household)
(reg-event-db :confirmed-remove-household confirmed-remove-household)
(reg-event-db :confirmed-edit-household confirmed-edit-household)
(reg-event-db :set-households set-households)
