(ns chorechart.resty.living-situations
  (:require [chorechart.db.core :as db]))

(defn remove [params]
  (let [{:keys [living_situation_id]} params]
    (db/remove-living-situation! {:living_situation_id living_situation_id})
    (list {:living_situation_id living_situation_id})))
