(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]
            [chorechart.db.core :as db]))

(defn authenticated-resty [req route-fn]
  (if (authenticated? req)
    (let [{ :keys [params session] } req
          { :keys [person_id] } params]
      (if (= (get-in session [:person :id]) person_id)
        (route-fn params)
        (str "you can't access that") ;; tried to use a person_id that isn't their session identity
        )
      )
    (response/found "/login/no-auth"))) ;; not signed in

(defn authenticated-route [req route-fn]
  (if (authenticated? req)
    (route-fn req)
    (response/found "/login/no-auth")))

(defn home-page [req]
  (let [{:keys [session]} req
        user_name (:identity session)
        person (:person session)]
    (layout/render "home.html" {:user_name user_name :person person})))

(defn add-household [] (str "not done"))
(defn add-living-situation [] (str "not done"))
(defn add-chore [] (str "not done"))

(defn chart-entry [] (str "not done"))
(defn chart-entry-edit [] (str "not done"))
(defn chart-entry-remove [] (str "not done"))

;; TODO some kind of validation that the session id can alter/view stuff

(defn view-chart [params]
  (let [{:keys [person_id date]} params]
    (db/list-chart-entries {:person_id person_id})))
(defn view-chores [person_id]
  (db/list-chores {:person_id person_id}))
(defn view-households [params]
  (db/list-households {:person_id (:person_id params)}))

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
