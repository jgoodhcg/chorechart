(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]))

(defn home-page [req]
  (if-not (authenticated? req)
    (auth/login-page {:flash "You have to login first"})
    (layout/render "home.html")))

(defroutes auth-routes
  (GET "/signup" [] (auth/signup-page))
  (POST "/signup" [] auth/signup)
  (GET "/login"  [] (auth/login-page))
  (POST "/login" [] auth/login))

(defn add-household [] (str "not done"))
(defn add-living-situation [] (str "not done"))
(defn add-chore [] (str "not done"))

(defn chart-entry [] (str "not done"))
(defn chart-entry-edit [] (str "not done"))
(defn chart-entry-remove [] (str "not done"))

(defn view-chart [] (str "not done"))
(defn view-chores [req] (str "not done"))
(defn view-households [req]
  (let [{:keys [params]} req
         {:keys [user_name]} params]
     (response/ok {:user-name user_name})
     )
   )

(defroutes home-routes
  (GET "/" [] home-page)

  (POST "/add/household" [] add-household)
  (POST "/add/living-situation" [] add-living-situation)
  (POST "/add/chore" [] add-chore)

  (POST "/chart/entry" [] chart-entry)
  (POST "/chart/entry/edit" [] chart-entry-edit)
  (POST "/chart/entry/remove" [] chart-entry-remove)

  (POST "/view/chart" [] view-chart)
  (POST "/view/chores" [] view-chores)
  (POST "/view/households" [] view-households)
  )
