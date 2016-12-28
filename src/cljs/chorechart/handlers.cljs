(ns chorechart.handlers
  (:require [chorechart.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.async-flow-fx]
            [ajax.core :as ajax]
            [chorechart.misc :as misc]
            [cljs.pprint :refer [pprint]]
            [re-frame.core :refer
             [dispatch reg-event-db reg-event-fx path]]

            [chorechart.handlers.chart]
            [chorechart.handlers.households]
            [chorechart.handlers.chores]
            ))

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
              :dispatch-n [[:get-chores] [:get-chart] [:detect-new-account]]}]}}))

;; this should always run after set-selected-household
(reg-event-db
 :detect-new-account
 (fn [db [_ _]]
   (let [selected-household (:selected-household db)]
     (assoc db
            :new-account
            (or
             (nil? selected-household)
             (empty? selected-household))))))

(reg-event-db
 :set-person
 (fn [db [_ _]]
      (assoc db
             :id (.-id js/person)
             :user_name (.-user_name js/person)
             :email (.-email js/person))))

(reg-event-db
 :post-resp
 (fn [db [a b]]
   (pprint a)
   (pprint b)
   (if-let [new (:new db)]
     (assoc db :new (+ new 1))
     (assoc db :new 1))))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
 :print-db
 (fn [db [_ _]]
   (pprint db)
   db))
