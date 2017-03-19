(ns chorechart.resty.roomates
  (:require [chorechart.db.core :as db]
            [ring.util.http-response :as response]
            [clojure.string :as str]
            ))

(defn view [params]
  (db/list-roomates {:living_situation_id (:living_situation_id params)}))

(defn add [params]
  (let [{:keys [roomate_email living_situation_id]} params
        roomate_email_no_case (str/lower-case roomate_email)]

    (if-let [person (db/find-person {:email roomate_email_no_case})]
      (let [new_living_situation_id (db/add-roomate!
                                     {:roomate_email roomate_email_no_case
                                      :living_situation_id living_situation_id})]
        (list (assoc (select-keys person [:user_name])
                     :living_situation new_living_situation_id)))
      (response/unprocessable-entity)
      )))
