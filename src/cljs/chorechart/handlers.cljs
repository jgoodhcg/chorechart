(ns chorechart.handlers
  (:require [chorechart.db :as db]
            [cljs.pprint :refer [pprint]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx path]]))

(reg-event-db
  :initialize-db
  (fn [db [_ a]]
    db/default-db))

;; (reg-event-db
;;  :get-households
;;  (fn [db [_ _]]
;;    (do
;;      (pprint "making a post")
;;      (if-let [new (:new db)]
;;        (assoc db :new (+ new 1))
;;        (assoc db :new 1))
;;      )))

(reg-event-fx
 :get-households
 (fn [_world [_ val]]
   {:http-xhrio {:method          :post
                 :uri             "/view/households"
                 :params          {:user_name js/user_name}
                 :timeout         5000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:post-resp]
                 :on-failure      [:post-resp]}}))

(reg-event-db
 :post-resp
 (fn [db [a b]]
   (pprint a)
   (pprint b)
   (pprint db)
   (if-let [new (:new db)]
     (assoc db :new (+ new 1))
     (assoc db :new 1))))

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
