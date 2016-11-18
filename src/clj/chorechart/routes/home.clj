(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]))

(defn authenticated-route [req route-fn]
  (if (authenticated? req)
    (route-fn req)
    (response/found "/login/no-auth")))

(defn home-page [req]
  (let [{:keys [session]} req]
    (layout/render "home.html" {:user_name (:identity session)}))
    )

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
     ()
     )
   )

(defroutes auth-routes
  (GET "/signup" [] (auth/signup-page))
  (POST "/signup" [] auth/signup)
  (GET "/login"  [] (auth/login-page ""))
  (GET "/login/:error"  [error] (auth/login-page error))
  (POST "/login" [] auth/login))

(defroutes home-routes
  (GET "/" req (authenticated-route req home-page))

  (POST "/add/household" [] add-household)
  (POST "/add/living-situation" [] add-living-situation)
  (POST "/add/chore" [] add-chore)

  (POST "/chart/entry" [] chart-entry)
  (POST "/chart/entry/edit" [] chart-entry-edit)
  (POST "/chart/entry/remove" [] chart-entry-remove)

  (POST "/view/chart" [] view-chart)
  (POST "/view/chores" [] view-chores)
  (POST "/view/households" req (authenticated-route req view-households))
  )
