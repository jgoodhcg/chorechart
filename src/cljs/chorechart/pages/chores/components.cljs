(ns chorechart.pages.chores.components
  (:require [chorechart.pages.misc-comps.row-cases :refer [row-case-options row-case-edit]]))

(defn chore-row [index chore options-pressed]
  (let [this_options_pressed (nth @options-pressed index)]

    [:div.list-group-item
     {:key index}

     (case this_options_pressed
       :options (row-case-options
                 options-pressed
                 index
                 :remove-chore
                 (:id chore))

       :edit (row-case-edit
              options-pressed
              index
              (str (:chore_name chore))
              :set-pending-edit-chore
              (fn [val] {:new_chore_name
                         val
                         :chore_id
                         (:id
                          chore)})
              :edit-chore)

       :normal [:div.row
                [:div.col-xs-9.list-group-item-heading
                 (:chore_name chore)]
                [:div.col-xs-3
                 [:button.btn.btn-sm.btn-secondary
                  {:on-click
                   #(swap!
                     options-pressed assoc index :options)}
                  "options"]]])]))
