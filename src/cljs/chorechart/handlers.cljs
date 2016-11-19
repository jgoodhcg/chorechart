(ns chorechart.handlers
  (:require [chorechart.db :as db]
            [re-frame.core :refer [dispatch reg-event-db path]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    ;; {:http {:method :post
    ;;         :url "/view-households"
    ;;         :on-success [:set-households]
    ;;         :on-fail    [:set-households]}
    ;;  :db db/default-db}))
    db/default-db))

(reg-event-db
 :set-households
 (fn [db [_ households]]
   (assoc db :households households)))

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
