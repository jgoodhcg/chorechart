(ns chorechart.pages.misc-comps.nav
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

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
         [:a.navbar-brand {:href "#/"} "Chorechart"]]
        [:div.col-xs-4
         [:ul.nav.navbar-nav.hidden-md-up
          [:li.nav-item.active
           [:a.nav-link {:href (str "#/" (name @selected-page))} @selected-page]]]]
        [:div.col-xs-2
         [:input.hidden-md-up.btn.btn-sm
          {:type "button" :value "☰" :on-click #(swap! collapsed? not) }]]]
       [:div.row
        [:div.col-xs-12
         [:div.collapse.navbar-toggleable-sm
          (when-not @collapsed? {:class "in"})
          [:ul.nav.navbar-nav
           [nav-link "#/chart" "Chart" :chart collapsed?]
           [nav-link "#/households" "Households" :households collapsed?]
           [nav-link "#/roomates" "People" :roomates collapsed?]
           [nav-link "#/chores" "Chores" :chores collapsed?]
           [nav-link "#/info" "Info" :info collapsed?]
           [:li.nav-item
            [:a.nav-link
             {:href "/logout"} "Logout"]]]]]]])))

