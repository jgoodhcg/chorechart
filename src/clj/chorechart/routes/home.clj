(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [chorechart.db.core :refer [*db*] :as db]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
       (home-page))

  (POST "/add" []
    (fn [req]
      (let [chore (get-in req [:params :chore])
            name (get-in req [:params :name])
            date (get-in req [:params :date])]
        (str (db/ins-chore! {:chore chore :person name :completed date}))
        )
      )
    )

  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
       (response/header "Content-Type" "text/plain; charset=utf-8"))))

