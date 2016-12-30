(ns chorechart.routes.definition
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]
            [chorechart.resty.households :as households]
            [chorechart.db.core :as db]))

(defn failed-authentication-handler [request _]
  (response/found "/login/no-auth"))

(defn home-page [req]
  (let [{:keys [session]} req
        email (:identity session)
        person (:person session)]
    (layout/render "home.html" {:email email :person person})))

(defn remove-living-situation [params]
  (let [{:keys [living_situation_id]} params]
    (db/remove-living-situation! {:living_situation_id living_situation_id})
    (list {:living_situation_id living_situation_id})))

(defn remove-chore [params]
  (let [{:keys [chore_id]} params]
    (db/remove-chore! {:chore_id chore_id})
    (list {:chore_id chore_id})))

(defn edit-chore [params]
  (let [{:keys [new_chore_name chore_id]} params]
    (list (db/edit-chore!
           {:new_chore_name new_chore_name
            :chore_id chore_id}))))

(defn add-chore [params]
  (let [{:keys [chore_name household_id]} params]
    (list (db/add-chore! {:chore_name chore_name :household_id household_id
                          :description "default description nobody made manually"}))))

(defn add-roomate [params]
  (let [{:keys [roomate_email living_situation_id]} params]
    (if-let [person (db/find-person {:email roomate_email})]
      (let [new_living_situation_id (db/add-roomate!
                                     {:roomate_email roomate_email
                                      :living_situation_id living_situation_id})]
        (list (assoc (select-keys person [:user_name])
                     :living_situation new_living_situation_id))))))

(defn chart-entry [params]
  (let [{:keys [chore_id living_situation_id moment]} params]
    (list (db/add-chart-entry! {:living_situation_id living_situation_id
                               :chore_id chore_id
                               :moment moment}))))

(defn chart-entry-remove [params]
  (let [chart_id (:chart_id params)]
    (if (= 1 (db/remove-chart-entry! {:chart_id chart_id}))
      (list {:chart_id chart_id}))))

(defn view-chart [params]
  (let [{:keys [household_id date]} params]
    (db/list-chart-entries
     {:household_id household_id :date_from date})))

(defn view-chores [params]
  (db/list-chores {:household_id (:household_id params)}))

(defn view-roomates [params]
  (db/list-roomates {:living_situation_id (:living_situation_id params)}))

(defroutes auth-routes

  (GET "/login/:error"  [error] (auth/login-page error))

  (GET "/signup" [] (auth/signup-page))
  (GET "/login"  [] (auth/login-page ""))

  (POST "/signup" [] auth/signup)
  (POST "/login"  [] auth/login)
  (GET "/logout"  [] auth/logout))


(defroutes post-routes
  (POST "/households/add"  req (households/add   (:params req)))
  (POST "/households/view" req (households/view  (:params req)))
  (POST "/households/edit" req (households/edit  (:params req)))

  (POST "/chores/add"    req (add-chore    (:params req)))
  (POST "/chores/remove" req (remove-chore (:params req)))
  (POST "/chores/edit"   req (edit-chore   (:params req)))
  (POST "/chores/view"   req (view-chores  (:params req)))

  (POST "/living-situations/remove" req (remove-living-situation (:params req)))

  (POST "/roomates/view" req (view-roomates (:params req)))
  (POST "/roomates/add"  req (add-roomate   (:params req)))

  (POST "/chart/add"    req (chart-entry        (:params req)))
  (POST "/chart/remove" req (chart-entry-remove (:params req)))
  (POST "/chart/view"   req (view-chart         (:params req))))

(defroutes home-routes
  (GET "/" req (home-page req)))
