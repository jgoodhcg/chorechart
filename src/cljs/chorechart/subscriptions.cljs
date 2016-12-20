(ns chorechart.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
 :user_name
 (fn [db _]
   (:user_name db)))

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
   (:selected-household db)))

(reg-sub
 :new-account
 (fn [db _]
   (:new-account db)))

(reg-sub
 :chart
 (fn [db _]
   (:chart db)))

(reg-sub
 :chores
 (fn [db _]
   (:chores db)))
