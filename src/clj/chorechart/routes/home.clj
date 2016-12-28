(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]
            [chorechart.db.core :as db]))

(defn authenticated-post [req route-fn]
  (if (authenticated? req)
    (let [{:keys [params]} req]
      (route-fn params))
    (response/found "/login/no-auth")))

(defn authenticated-route [req route-fn]
  (if (authenticated? req)
    (route-fn req)
    (response/found "/login/no-auth")))

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

(defn edit-household [params]
  (let [{:keys [new_house_name living_situation_id]} params]
    (list (db/edit-household!
           {:new_house_name new_house_name
            :living_situation_id living_situation_id}))))

(defn edit-chore [params]
  (let [{:keys [new_chore_name chore_id]} params]
    (list (db/edit-chore!
           {:new_chore_name new_chore_name
            :chore_id chore_id}))))

(defn add-household [params]
  (let [{:keys [house_name person_id]} params]
    (if-let [household_id (:id (db/add-household! {:house_name house_name}))]
      (list (assoc (db/add-living-situation!
                    {:person_id person_id :household_id household_id})
                   :household_id household_id :house_name house_name))
      (response/not-found "error entering household"))))

(defn add-living-situation [] (str "not done"))

(defn add-chore [params]
  (let [{:keys [chore_name household_id]} params]
    (list (db/add-chore! {:chore_name chore_name :household_id household_id
                          :description "default description nobody made manually"}))
    ))

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
(defn chart-entry-edit [] (str "not done"))
(defn chart-entry-remove [params]
  (let [chart_id (:chart_id params)]
    (if (= 1 (db/remove-chart-entry! {:chart_id chart_id}))
      (list {:chart_id chart_id}))
    ))

(defn view-chart [params]
  (let [{:keys [household_id date]} params]
    (db/list-chart-entries
     {:household_id household_id :date_from date})))
(defn view-chores [params]
  (db/list-chores {:household_id (:household_id params)}))
(defn view-households [params]
  (db/list-households {:person_id (:person_id params)}))
(defn view-roomates [params]
  (db/list-roomates {:living_situation_id (:living_situation_id params)}))

(defroutes auth-routes
  (GET "/signup" [] (auth/signup-page))
  (POST "/signup" [] auth/signup)
  (GET "/login"  [] (auth/login-page ""))
  (GET "/login/:error"  [error] (auth/login-page error))
  (POST "/login" [] auth/login)
  (GET "/logout" [] auth/logout))

(defroutes post-routes
  (POST "/households/add" req (authenticated-post req add-household))
  (POST "/households/view" req (authenticated-post req view-households))
  (POST "/households/edit" req (authenticated-post req edit-household))

  (POST "/chores/add" req (authenticated-post req add-chore))
  (POST "/chores/remove" req (authenticated-post req remove-chore))
  (POST "/chores/edit" req (authenticated-post req edit-chore))
  (POST "/chores/view" req (authenticated-post req view-chores))

  (POST "/living-situations/remove" req (authenticated-post req remove-living-situation))
  (POST "/living-situations/add" [] add-living-situation)

  (POST "/roomates/view" req (authenticated-post req view-roomates))
  (POST "/roomates/add" req (authenticated-post req add-roomate))

  (POST "/chart/add" req (authenticated-post req chart-entry))
  (POST "/chart/remove" req (authenticated-post req chart-entry-remove))
  (POST "/chart/view" req (authenticated-post req view-chart))
  )

(defroutes home-routes
  (GET "/" req (authenticated-route req home-page)))
