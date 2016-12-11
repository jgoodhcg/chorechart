(ns chorechart.handlers
  (:require [chorechart.db :as db]
            [cljs.pprint :refer [pprint]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx path reg-fx]]))

;; (reg-fx
;;  :dispatch
;;  (fn [dispatch-vec]
;;    (dispatch dispatch-vec)))
;; does this already exist?

(reg-event-db
 :print-db
 (fn [db [_ _]]
   (pprint db)
   db))

(reg-event-db
  :initialize-db
  (fn [db [_ a]]
    db/default-db))

(reg-event-db
 :set-person
 (fn [db [_ _]]
      (assoc db
             :id (.-id js/person))))

(reg-event-fx
 :get-households
 (fn [_world [_ _]]
    {:http-xhrio
     {:method          :post
      :uri             "/view/households"
      :params          {:person_id (get-in _world [:db :id])}
      :timeout         5000
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [:set-households]
      :on-failure      [:post-resp]}}))

(reg-event-fx
 :get-chart
 (fn [_world [_ _]]
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
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :get-chores
 (fn [_world [_ _]]
   {:http-xhrio
    {:method          :post
     :uri             "/view/chores"
     :params          {:household_id
                       (get-in _world [:db :selected-household :id])}
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:set-chores]
     :on-failure      [:post-resp]}}))

(reg-event-db
 :set-pending-chore-id
 (fn [db [_ chore_id]]
   (assoc-in db [:pending-chart-entry :chore_id] chore_id)))

(reg-event-db
 :set-pending-date
 (fn [db [_ date]]
   (assoc-in db [:pending-chart-entry :moment] date)))

(reg-event-db
 :set-pending-living-situation
 (fn [db [_ _]]
   (assoc-in db [:pending-chart-entry :living_situation_id]
            (get-in db [:selected-household :living_situation_id]))))

(reg-event-fx
 :send-chart-entry
 (fn [_world [_ _]]
   {:http-xhrio
    {:method          :post
     :uri             "/chart/entry"
     :params          (get-in _world [:db :pending-chart-entry])
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:confirmed-chart-entry]
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :add-household
 (fn [_world [_ _]]
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
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :add-roomate
 (fn [_world [_ _]]
   {:http-xhrio
    {:method          :post
     :uri             "/add/roomate"
     :params          (get-in _world [:db :pending-add-roomate])
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:confirmed-add-roomate]
     :on-failure      [:post-resp]}}))

(reg-event-db
 :confirmed-add-roomate
 (fn [db [_ new_roomate]]
   (-> db
       (assoc :pending-add-roomate {})
       (assoc-in [:selected-household :roomates] new_roomate))))

(reg-event-db
 :confirmed-add-household
 (fn [db [_ new_household]]
   (assoc db
          :pending-add-household {}
          :households (conj (:households db) new_household))
   ))

(reg-event-db
 :set-pending-household
 (fn [db [_ house_name]]
   (assoc db :pending-add-household {:house_name house_name})))

(reg-event-db
 :set-pending-roomate
 (fn [db [_ roomate_email]]
   (assoc db :pending-add-roomate
          {:roomate_email roomate_email
           :living_situation_id
           (get-in db [:selected-household :living_situation_id])})))

(reg-event-db
 :set-pending-edit-household
 (fn [db [_ info]]
   (assoc db :pending-edit-household
          (select-keys info [:new_house_name :living_situation_id]))))

(reg-event-fx
 :edit-household
 (fn [_world [_ _]]
   {:http-xhrio
    {:method          :post
     :uri             "/edit/household"
     :params          (select-keys (get-in _world [:db :pending-edit-household])
                                   [:new_house_name :living_situation_id])
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:confirmed-edit-household]
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :remove-household
 (fn [_world [_ living_situation_id]]
   {:http-xhrio
    {:method          :post
     :uri             "/remove/living-situation"
     :params          {:living_situation_id living_situation_id}
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:confirmed-remove-household]
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :get-roomates-selected-household
 (fn [_world [_ living_situation_id]]
   {:http-xhrio
    {:method          :post
     :uri             "/view/roomates"
     :params          (get-in _world [:db :selected-household])
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [:set-roomates-selected-household]
     :on-failure      [:post-resp]}}))

(reg-event-db
 :set-roomates-selected-household
 (fn [db [_ roomates]]
   (if (empty? roomates)
     db
     (assoc-in db [:selected-household :roomates] roomates)
     )
   ))

(reg-event-db
 :confirmed-remove-household
 (fn [db [a household_gone]]
   (assoc db :households
          (first (filter #(not (= (:living_situation_id household_gone)
                             (get % :living_situation_id)))
                         (:households db))))))

(reg-event-db
 :confirmed-edit-household
 (fn [db [a b]]
   (pprint "confirmed edit household")
   db))

(reg-event-db
 :confirmed-chart-entry
 (fn [db [a b]]
   (assoc db :pending-chart-entry {})))

(reg-event-db
 :post-resp
 (fn [db [a b]]
   (pprint a)
   (pprint b)
   (if-let [new (:new db)]
     (assoc db :new (+ new 1))
     (assoc db :new 1))))

(reg-event-db
 :set-households
 (fn [db [_ households]]
   (assoc db :households households)))

(reg-event-db
 :set-selected-household
 (fn [db [_ selected_living_situation_id]]
   (assoc db :selected-household
          (first (filter #(= selected_living_situation_id
                           (get % :living_situation_id))
                       (:households db))))))

(reg-event-db
 :set-chart
 (fn [db [_ chart]]
   (assoc db :chart chart)))

(reg-event-db
 :set-chores
 (fn [db [_ chores]]
   (assoc db :chores chores)))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(reg-event-db
 :set-chore
 (path [:current :chore])
       (fn [old-chore [_ new-chore]]
         new-chore))

(reg-event-db
 :set-name
 (path [:current :name])
       (fn [old-name [_ new-name]]
         new-name))

(reg-event-db
 :set-date
 (path [:current :date])
       (fn [old-date [_ new-date]]
         new-date))
