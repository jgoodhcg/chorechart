(ns chorechart.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
 :docs
 (fn [db _]
   (:docs db)))

(reg-sub
 :current
 (fn [db _]
   (:current db)))

(reg-sub
 :households
 (fn [db _]
   (:households db)))

(reg-sub
 :selected-household
 (fn [db _]
   (let [sh (:selected-household db)]
     (if (empty? sh)
       (first (:households db))
       sh
       ))))

(reg-sub
 :chart
 (fn [db _]
   (:chart db)))

(reg-sub
 :chores
 (fn [db _]
   (:chores db)))
