(ns chorechart.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [cljs.pprint :refer [pprint]]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [chorechart.ajax :refer [load-interceptors!]]
            [chorechart.handlers]
            [chorechart.subscriptions])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
    (let [selected-page (rf/subscribe [:page])]
      [:nav.navbar.navbar-light.bg-faded {:style {:border-radius "0em"}}
       [:div.row
        [:div.col-xs-6
         [:a.navbar-brand {:href "#/"} "chorechart"]
         ]
        [:div.col-xs-4
         [:ul.nav.navbar-nav.hidden-md-up
          [:li.nav-item.active
           [:a.nav-link {:href (str "#/" (name @selected-page))} @selected-page]]]
         ]
        [:div.col-xs-2
         [:input.hidden-md-up.btn.btn-sm
          {:type "button" :value "☰" :on-click #(swap! collapsed? not) } ]]]
       [:div.row
        [:div.col-xs-12
         [:div.collapse.navbar-toggleable-sm
          (when-not @collapsed? {:class "in"})
          [:ul.nav.navbar-nav
           [nav-link "#/" "Home" :home collapsed?]
           [nav-link "#/chart" "chart" :chart collapsed?]
           [nav-link "#/households" "Households" :households collapsed?]
           ]]
         ]]
       ])))

(defn row [label input]
  [:div.row
   [:div.col-xs-2 [:label label]]
   [:div.col-xs-5 input]])

(defn input [label name type dispatch-key]
  (row label [:input.form-control {:type type
                                   :name name
                                   :on-change
                                   #(rf/dispatch [dispatch-key (-> % .-target .-value)])}]))

(defn select [name dispatch-key options]
  [:div
   [:select.form-control
    {:name name
     :defaultValue "default"
     :style {:width "100%"}
     :on-change #(rf/dispatch
                  [dispatch-key (-> % .-target .-value int)])}
    (cons
     [:option
      {:value "default" :disabled true :key (str -1)}
      "-- choose an option --"]
     (mapv
      #(-> [:option
            {:value (:value %) :key (str (.indexOf options %)) }
            (:label %)]) options))]])

(defn get-btn [dispatch-key]
  [:button.btn
   {:on-click
      #(do
        (pprint (str "dispatched " dispatch-key))
        (rf/dispatch [dispatch-key]))}
     (str dispatch-key)])

(defn print-btn []
  [:button.btn
   {:on-click
    #(do
       (pprint "print pressed")
       (rf/dispatch [:print-db])
       )
    }
   "print"])

(defn home-page []
    [:div.container
     (str js/user_name " home page")
     [:br]
     (get-btn :get-households)
     (get-btn :get-chart)
     (get-btn :get-chores)
     (print-btn)
     ]
  )

(defn household-row [index household options-pressed]
  (let [this_options_pressed (nth @options-pressed index)
        selected_household (rf/subscribe [:selected-household])
        is_selected (= (:living_situation_id household)
                       (:living_situation_id @selected_household))]

    (case this_options_pressed
      :options [:tr {:key index}
                [:td
                 [:button.btn.btn-sm
                  {:on-click #(swap! options-pressed assoc index :edit)}
                  "edit"]]
                [:td
                 [:button.btn.btn-sm.btn-danger
                  {:on-click #(pprint %)}
                  "delete"]]
                [:td
                 [:button.btn.btn-sm.btn-secondary
                  {:on-click #(swap! options-pressed assoc index :normal)}
                  "cancel"]]]

      :edit [:tr {:key index}
             [:td [:input
                   {:type "text"
                    :on-change #(rf/dispatch
                                [:set-pending-edit-household
                                 {:new_house_name (-> % .-target .-value)
                                  :living_situation_id
                                  (:living_situation_id household)}])}]]
             [:td [:button.btn.btn-sm {:on-click
                                       #(do
                                         (rf/dispatch [:edit-household])
                                         (swap! options-pressed assoc index :normal))}
                   "submit"]]
             [:td [:button.btn.btn-sm.btn-secondary {:on-click
                                         #(swap! options-pressed assoc index :normal)}
                   "cancel"]]]

      :normal [(if is_selected :tr.table-active :tr) {:key index}
               [:td (:house_name household)]
               [:td ] ;; needs three cells
               [:td
                [:button.btn.btn-sm.btn-secondary
                 {:on-click #(swap! options-pressed assoc index :options)}
                 "options"
                 ]]]
      )
    )
  )

(defn households-list [households]
  (r/with-let [options-pressed ;; vec to hold state for each household
               (r/atom (vec
                        (map
                         (fn [_] :normal)
                         households)))]
    [:table.table.table-responsive
     [:tbody
      (doall (map-indexed
              #(household-row %1 %2 options-pressed)
              households))
      ]
     ]
    )
  )

(defn households-add-new []
  (r/with-let [add-new-pressed (r/atom false)
               house-name-input "house-name-input"]
    (if @add-new-pressed
      ;; new form
      [:div.row
       [:div.col-xs-12
        [:div.row
         [:div.col-xs-12.col-sm-9.form-group
          [:input.form-control
           {:type "text" :id house-name-input :placeholder "new household name"
            :on-change
            #(rf/dispatch [:set-pending-household (-> % .-target .-value)])}]]
         [:div.col-xs-12.col-sm-3.form-group
          [:input.btn.btn-primary.btn-block
           {:type "button" :value "submit"
            :on-click
            #(do
               (reset! add-new-pressed false)
               (rf/dispatch [:add-household]))}]
          [:input.btn.btn-secondary.btn-block
           {:type "button" :value "cancel"
            :on-click #(reset! add-new-pressed false)}]]]]]

      ;; button only
      [:div.row
       [:div.col-xs-12.form-group
        [:input.btn.btn-primary.btn-block
         {:type "button" :value "add new household"
          :on-click #(reset! add-new-pressed true)}]
        ]])))

(defn households-page []
  (rf/dispatch [:get-households])
  (let [households (rf/subscribe [:households])]
    [:div.container
     [:div.row
      [:div.col-xs-12
       [:h2 "Households"]
       [:div
        (if (> (count @households) 0)
          (households-list @households)
          "no households yet ):")
        ]]]
     (households-add-new)
     ])
  )

(defn chart-table [chart]
  [:div.row
   [:div.col-xs-12
    [:table.table
     [:thead
      [:tr [:th "Person"] [:th "Chore"] [:th "Date"]]]
     [:tbody
      (map
       #(vec [:tr {:key (.indexOf chart %)}
              [:td (:user_name %)]
              [:td (:chore_name %)]
              [:td (subs (:moment %) 5 10)]])
       chart)]]]])

(defn chart-input [chores]
  (r/with-let [collapsed (r/atom false)]
    [:div.container-fluid.bg-faded {:style { :width "100%" :padding-top "1em"
                                            :position "fixed" :bottom "0em" :left "0em" :right "0em"}}
    (if-not @collapsed
       [:div.row
        [:div.row
         [:div.col-xs-1.offset-xs-10.form-group
         [:input.btn.btn-sm {:type "button" :value "▼"
                                  :on-click #(reset! collapsed true)}]]]
        [:div.row
         [:div.col-xs-12-down.col-sm-4.form-group
          [:input.form-control {:type "text" :disabled true :style {:width "100%"} :value "Name"}]]
         [:div.col-xs-12.col-sm-4.form-group
          (select "name" :set-pending-chore-id
                  (map
                   #(hash-map :value (:id %) :label (:chore_name %)) ;; format chore maps for select fn
                   chores))]
         [:div.col-xs-12.col-sm-4.form-group
          [:input.form-control
           {:type "date" :style {:width "100%"}
            :on-change #(rf/dispatch [:set-pending-date (-> % .-target .-value)])}]]
         [:div.col-xs-12.col-sm-12.form-group
          [:input.btn.btn-primary.btn-block
           {:type "button" :value "submit" :width "100%"
            :on-click #(rf/dispatch [:send-chart-entry])}]]]]

       ;; collapsed
       [:div.row
        [:div.col-xs-1.offset-xs-10.form-group
         [:input.btn.btn-sm {:type "button" :value "▲"
                             :on-click #(reset! collapsed false)}]]])]))

(defn chart-page []
  (let [chart (rf/subscribe [:chart])
        chores (rf/subscribe [:chores])]
    (rf/dispatch [:set-pending-living-situation])
    [:div.container-fluid
     (chart-table @chart)
     (chart-input @chores)]))

(def pages
  {:home #'home-page
   :chart #'chart-page
   :households #'households-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/households" []
  (rf/dispatch [:set-active-page :households]))

(secretary/defroute "/chart" []
  (rf/dispatch [:set-active-page :chart]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components)
  (rf/dispatch [:set-person])
  )
