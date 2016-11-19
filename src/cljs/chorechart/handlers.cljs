(ns chorechart.handlers
  (:require [chorechart.db :as db]
            [cljs.pprint :refer [pprint]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx path]]))

(reg-event-db
 :print-db
 (fn [db [_ _]]
   (pprint db)
   db))

(reg-event-db
  :initialize-db
  (fn [db [_ a]]
    db/default-db))

(reg-event-fx
 :get-all-user-state
 (fn [_world [_ val]]
    {:http-xhrio [
                  {:method          :post
                   :uri             "/view/chores"
                   :params          {:user_name js/user_name}
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:set-chores]
                   :on-failure      [:post-resp]}
                  {:method          :post
                   :uri             "/view/households"
                   :params          {:user_name js/user_name}
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:set-households]
                   :on-failure      [:post-resp]}
                  ] }
   ))


(reg-event-fx
 :get-households
 (fn [_world [_ val]]
   {:http-xhrio {:method          :post
                 :uri             "/view/households"
                 :params          {:user_name js/user_name}
                 :timeout         5000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:set-households]
                 :on-failure      [:post-resp]}}))

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
