(ns chorechart.pages.misc-comps.list
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn generic-list [items row-comp-fn]
  (r/with-let
    [options-pressed ;; vec to hold local options state
     (r/atom (vec
              (map
               (fn [_] :normal) ;; default option state
               items)))]

    ;; following adds default option state for new items since last render
    (if (>
         (count items)
         (count @options-pressed))
      (swap! options-pressed conj :normal))

    [:div.list-group
     (doall (map-indexed
             #(row-comp-fn %1 %2 options-pressed)
             items))]))
