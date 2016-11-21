(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]
            [chorechart.db.core :as db]))

(defn authenticated-resty [req route-fn]
  (if (authenticated? req)
    (let [{ :keys [params] } req
          { :keys [user_name] } params]
      (if (= (:identity req) user_name)
        (route-fn user_name)
        (str "you can't access that") ;; tried to use a user_name that isn't their identity
        ))
    (response/found "/login/no-auth"))) ;; not signed in

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

(defn view-chart [user_name]
  (db/list-households {:user_name user_name}))
(defn view-chores [user_name]
  (db/list-households {:user_name user_name}))
(defn view-households [user_name]
  (db/list-households {:user_name user_name}))

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

  (POST "/view/chart" req (authenticated-resty req view-chart))
  (POST "/view/chores" req (authenticated-resty req view-chores))
  (POST "/view/households" req (authenticated-resty req view-households)) ;; TODO put authenticated-route in middleware
  )
