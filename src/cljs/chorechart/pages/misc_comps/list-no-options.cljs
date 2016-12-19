(ns chorechart.pages.misc-comps.list-no-options)

(defn generic-row [index thing display-key]
  [:div.list-group-item {:key index}
   [:h5 (display-key thing)]])

(defn generic-list-no-options [things display-key]
  [:div.list-group
   (doall (map-indexed
           #(generic-row %1 %2 display-key) things))])
