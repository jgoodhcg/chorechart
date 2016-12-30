(ns chorechart.resty.households
  (:require [ring.util.http-response :as response]
            [chorechart.db.core :as db]))

(defn edit [params]
  (let [{:keys [new_house_name living_situation_id]} params]
    (list (db/edit-household!
           {:new_house_name new_house_name
            :living_situation_id living_situation_id}))))

(defn add [params]
  (let [{:keys [house_name person_id]} params]
    (if-let [household_id (:id (db/add-household! {:house_name house_name}))]
      (list (assoc (db/add-living-situation!
                    {:person_id person_id :household_id household_id})
                   :household_id household_id :house_name house_name))
      (response/not-found "error entering household"))))

(defn view [params]
  (db/list-households {:person_id (:person_id params)}))
