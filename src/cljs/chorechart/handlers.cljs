(ns chorechart.handlers
  (:require [chorechart.db :as db]
            [cljs.pprint :refer [pprint]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [day8.re-frame.async-flow-fx]
            [re-frame.core :refer
             [dispatch reg-event-db reg-event-fx path reg-fx]]))

;; (reg-fx
;;  :dispatch
;;  (fn [dispatch-vec]
;;    (dispatch dispatch-vec)))
;; does this already exist?

(reg-event-fx
 :boot
 (fn [_ _]
   {:db db/default-db
    :async-flow
    {:first-dispatch [:set-person]
     :rules [
             {:when :seen? :events :set-person
              :dispatch [:get-households]}
             {:when :seen? :events :set-households
              :dispatch [:set-selected-household]}
             {:when :seen? :events :set-selected-household
              :dispatch-n [[:get-chores] [:get-chart] [:detect-new-account]]}
             ]}}))

;; this should always run after set-selected-household
(reg-event-db
 :detect-new-account
 (fn [db [_ _]]
   (if (empty? (:selected-household db))
       (assoc db :new-account true)  ;; new people should see info page first
     db
     )
   ))

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
                       (get-in _world
                               [:db :selected-household :household_id])}
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format
                       {:keywords? true})
     :on-success      [:set-chores]
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :remove-chore
 (fn [_world [_ chore_id]]
   {:http-xhrio
    {:method          :post
     :uri             "/remove/chore"
     :params          {:chore_id chore_id}
     :timeout         5000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format
                       {:keywords? true})
     :on-success      [:confirmed-remove-chore]
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :edit-chore
 (fn [_world [_ _]]
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
     :on-failure      [:post-resp]}}))

(reg-event-fx
 :add-chore
 (fn [_world [_ _]]
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
     :on-failure      [:post-resp]}}))

(reg-event-db
 :set-pending-add-chore
 (fn [db [_ chore_name]]
   (assoc db :pending-add-chore {:chore_name chore_name})))

(reg-event-db
 :confirmed-add-chore
 (fn [db [_ added_chore]]
   (assoc db :chores (conj (:chores db) added_chore))
   ))

(reg-event-db
 :confimred-edit-chore
 (fn [db [_ edited_chore]]
   (pprint "edited chore")
   (pprint edited_chore)
   db
 ))

(reg-event-db
 :confirmed-remove-chore
 (fn [db [a chore_gone]]
   db
   ;; (assoc db :chores
   ;;        (vec (filter #(not (= (:chore_id chore_gone)
   ;;                                (get % :id)))
   ;;                     (:chores db))))
   ))

(reg-event-db
 :set-pending-edit-chore
 (fn [db [_ chore]]
   (assoc db :pending-edit-chore chore)))

(reg-event-db
 :set-pending-chart-entry-chore-id
 (fn [db [_ chore_id]]
   (assoc-in db [:pending-chart-entry :chore_id] chore_id)))

(reg-event-db
 :set-pending-chart-entry-date
 (fn [db [_ date]]
   (assoc-in db [:pending-chart-entry :moment] date)))

(reg-event-db
 :set-pending-chart-entry-living-situation
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

(reg-event-fx
 :confirmed-add-household
 (fn [_world [_ new_household]]
   (let [db (:db _world)]
     {:db (assoc db
                 :pending-add-household {}
                 :households (conj (:households db) (first new_household)))
      :dispatch [:set-selected-household]} 
     )
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
     (assoc-in db [:selected-household :roomates] roomates))))

(reg-event-db
 :confirmed-remove-household
 (fn [db [_ household_gone]]
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
              selected_household)))))

(reg-event-db
 :confirmed-edit-household
 (fn [db [a b]]
   (pprint "confirmed edit household")
   db))

(reg-event-fx
 :confirmed-chart-entry
 (fn [_world [_ living_situation_id]]
   {:db (assoc (:db _world) :pending-chart-entry {})
    :dispatch [:get-chart]}))

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
   (let [selected_household (:selected-household db)
         households (:households db)]
     (assoc db :selected-household
            (if selected_living_situation_id
              (first
               (filter
                #(= selected_living_situation_id (get % :living_situation_id))
                households))
              (if (or (nil? selected_household)
                      (empty? selected_household))
                (first households)
                selected_household))))))

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
