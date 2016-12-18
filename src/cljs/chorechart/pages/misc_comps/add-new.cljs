(ns chorechart.pages.misc-comps.add-new
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn generic-add-new [placeholder
                       on-change-dispatch-key
                       submit-dispatch-key
                       add-new-button-text]

  (r/with-let [add-new-pressed (r/atom false)]

    (if @add-new-pressed

      ;; form to add new entry
      [:div.row
       [:div.col-xs-12
        [:div.row

         ;; input feild
         [:div.col-xs-12.col-sm-9.form-group
          [:input.form-control
           {:type "text" :placeholder placeholder
            :on-change
            #(rf/dispatch
              [on-change-dispatch-key
               (-> % .-target .-value)])}]]

         ;; submit button
         [:div.col-xs-12.col-sm-3.form-group
          [:input.btn.btn-primary.btn-block
           {:type "button" :value "submit"
            :on-click
            #(do
               (reset! add-new-pressed false)
               (rf/dispatch [submit-dispatch-key]))}]
          [:input.btn.btn-secondary.btn-block
           {:type "button" :value "cancel"
            :on-click #(reset! add-new-pressed false)}]]]]]

      ;; 'add new' button to show form
      [:div.row
       [:div.col-xs-12.form-group
        [:input.btn.btn-primary.btn-block
         {:type "button" :value add-new-button-text
          :on-click #(reset! add-new-pressed true)}]]])))
