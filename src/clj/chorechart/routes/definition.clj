(ns chorechart.routes.definition
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.db.core :as db]

            [chorechart.resty.auth :as auth]
            [chorechart.resty.households :as households]
            [chorechart.resty.chores :as chores]
            [chorechart.resty.living-situations :as living-situations]
            [chorechart.resty.roomates :as roomates]
            [chorechart.resty.chart :as chart]
            ))

(defn failed-authentication-handler [request _]
  (response/found "/login/no-auth"))

(defn home-page [req]
  (let [{:keys [session]} req
        email (:identity session)
        person (:person session)]
    (layout/render "home.html" {:email email :person person})))

(defroutes auth-routes
  (GET "/login/:error"  [error] (auth/login-page error))

  (GET "/signup" [] (auth/signup-page))
  (GET "/login"  [] (auth/login-page ""))

  (POST "/signup" [] auth/signup)
  (POST "/login"  [] auth/login)
  (GET "/logout"  [] auth/logout))

(defroutes post-routes
  (POST "/households/add"  req (households/add  (:params req)))
  (POST "/households/view" req (households/view (:params req)))
  (POST "/households/edit" req (households/edit (:params req)))

  (POST "/chores/add"    req (chores/add    (:params req)))
  (POST "/chores/remove" req (chores/remove (:params req)))
  (POST "/chores/edit"   req (chores/edit   (:params req)))
  (POST "/chores/view"   req (chores/view   (:params req)))

  (POST "/living-situations/remove" req (living-situations/remove
                                         (:params req)))

  (POST "/roomates/view" req (roomates/view (:params req)))
  (POST "/roomates/add"  req (roomates/add  (:params req)))

  (POST "/chart/add"    req (chart/add    (:params req)))
  (POST "/chart/remove" req (chart/remove (:params req)))
  (POST "/chart/view"   req (chart/view   (:params req))))

(defroutes home-routes
  (GET "/" req (home-page req)))
