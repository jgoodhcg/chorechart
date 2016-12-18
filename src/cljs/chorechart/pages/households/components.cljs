(ns chorechart.pages.households.components
  (:require [re-frame.core :as rf]))

(defn household-row [index household options-pressed]
  (let [this_options_pressed (nth @options-pressed index)
        selected_household (rf/subscribe
                            [:selected-household])
        is_selected (= (:living_situation_id household)
                       (:living_situation_id
                        @selected_household))]

    [:div.list-group-item
     {:key index
      :style (if is_selected
               {:background-color "#f4f4f5"}
               {})}

     (case this_options_pressed
       :options (row-case-options
                 options-pressed
                 index
                 :remove-household
                 (:living_situation_id household))

       :edit (row-case-edit
              options-pressed
              index
              (str (:house_name household))
              :set-pending-edit-household
              (fn [val] {:new_house_name
                         val
                         :living_situation_id
                         (:living_situation_id
                          household)})
              :edit-household)

       :normal [:div.row
                [:div.col-xs-1
                 (if (not is_selected)
                   [:button.btn.btn-sm
                    {:on-click
                     #(rf/dispatch
                       [:set-selected-household
                        (:living_situation_id household)])}]

                   [:button.btn.btn-sm.btn-primary])]
                [:div.col-xs-9.list-group-item-heading
                 (:house_name household)]
                [:div.col-xs-2
                 [:button.btn.btn-sm.btn-secondary
                  {:on-click
                   #(swap!
                     options-pressed assoc index :options)}
                  "options"]]])]))
