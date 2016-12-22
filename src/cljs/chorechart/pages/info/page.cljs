(ns chorechart.pages.info.page)

(defn info-page []
  [:div.container [:br]
   [:div.row
    [:div.col-xs-12
     [:h1 "Getting Started"]
     [:hr]
     [:ol
      [:li [:p "add a  "
            [:a.btn.btn-primary.btn-sm {:href "#/households"} "household"]]]
      [:li [:p "add chores for your selected household  "
            [:a.btn.btn-primary.btn-sm {:href "#/chores"} "add chores"]]]
      [:li
       [:p
        "invite roomates to your selected household via email (roomates must be signed up)  "
        [:a.btn.btn-primary.btn-sm {:href "#/roomates"} "invite roomates"]]]
      [:li [:p "add entries to your chart  "
            [:a.btn.btn-primary.btn-sm {:href "#/chart"} "add to chart"]]]]]]])
