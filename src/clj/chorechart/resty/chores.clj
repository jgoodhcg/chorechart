(ns chorechart.resty.chores
  (:require [chorechart.db.core :as db]
            [ring.util.http-response :as response]))

(defn view [params]
  (db/list-chores {:household_id (:household_id params)}))

(defn add [params]
  (let [{:keys [chore_name household_id]} params]
    (if-let [chore (db/add-chore! {:chore_name chore_name :household_id household_id
                                   :description "default description nobody made manually"})]
      (list chore)
      (response/unprocessable-entity))))

(defn edit [params]
  (let [{:keys [new_chore_name chore_id]} params]
    (list (db/edit-chore!
           {:new_chore_name new_chore_name
            :chore_id chore_id}))))

(defn remove [params]
  (let [{:keys [chore_id]} params]
    (db/remove-chore! {:chore_id chore_id})
    (list {:chore_id chore_id})))
