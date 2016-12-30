(ns chorechart.resty.chart
  (:require [chorechart.db.core :as db]))

(defn view [params]
  (let [{:keys [household_id date]} params]
    (db/list-chart-entries
     {:household_id household_id :date_from date})))

(defn remove [params]
  (let [chart_id (:chart_id params)]
    (if (= 1 (db/remove-chart-entry! {:chart_id chart_id}))
      (list {:chart_id chart_id}))))

(defn add [params]
  (let [{:keys [chore_id living_situation_id moment]} params]
    (list (db/add-chart-entry! {:living_situation_id living_situation_id
                                :chore_id chore_id
                                :moment moment}))))
