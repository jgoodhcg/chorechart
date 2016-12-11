(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [buddy.auth :refer [authenticated?]]
            [ring.util.http-response :as response]
            [chorechart.resty.auth :as auth]
            [chorechart.db.core :as db]))

(defn authenticated-resty [req route-fn]
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
        user_name (:identity session)
        person (:person session)]
    (layout/render "home.html" {:user_name user_name :person person})))

(defn remove-living-situation [params]
  (let [{:keys [living_situation_id]} params]
    (db/remove-living-situation! {:living_situation_id living_situation_id})
    (list {:living_situation_id living_situation_id})
    )
  )

(defn edit-household [params]
  (let [{:keys [new_house_name living_situation_id]} params]
    (list (db/edit-household!
           {:new_house_name new_house_name
            :living_situation_id living_situation_id}))
    )
  )

(defn add-household [params]
  (let [{:keys [house_name person_id]} params]
    (if-let [household_id (:id (db/add-household! {:house_name house_name}))]
      (list (assoc (db/add-living-situation!
                    {:person_id person_id :household_id household_id})
                   :household_id household_id :house_name house_name))
      (response/not-found "error entering household")
    ))
  )

(defn add-living-situation [] (str "not done"))
(defn add-chore [] (str "not done"))

(defn add-roomate [params]
  (let [{:keys [roomate_email living_situation_id]} params]
    (if-let [person (db/find-person {:email roomate_email})]
      (let [new_living_situation_id (db/add-roomate!
                                     {:roomate_email roomate_email
                                      :living_situation_id living_situation_id})]
        (list (assoc (select-keys person [:user_name])
                     :living_situation new_living_situation_id))
        )
      )
    )
  )

(defn chart-entry [params]
  (let [{:keys [chore_id living_situation_id moment]} params]
    (str (db/add-chart-entry! {:living_situation_id living_situation_id
                               :chore_id chore_id
                               :moment moment}))))
(defn chart-entry-edit [] (str "not done"))
(defn chart-entry-remove [] (str "not done"))

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
  (POST "/login" [] auth/login))

(defroutes home-routes
  (GET "/" req (authenticated-route req home-page))

  (POST "/add/household" req (authenticated-resty req add-household))
  (POST "/add/test" [] (hash-map :key "value"))
  (POST "/add/living-situation" [] add-living-situation)
  (POST "/add/chore" req (authenticated-resty req add-chore))

  (POST "/edit/household" req (authenticated-resty req edit-household))

  (POST "/remove/living-situation" req (authenticated-resty req remove-living-situation))

  (POST "/view/roomates" req (authenticated-resty req view-roomates))
  (POST "/add/roomate" req (authenticated-resty req add-roomate))

  (POST "/chart/entry" req (authenticated-resty req chart-entry))
  (POST "/chart/entry/edit" [] chart-entry-edit)
  (POST "/chart/entry/remove" [] chart-entry-remove)

  (POST "/view/chart" req (authenticated-resty req view-chart))
  (POST "/view/chores" req (authenticated-resty req view-chores))
  (POST "/view/households" req (authenticated-resty req view-households))
  ;; TODO put authenticated-route/resty in middleware
  )
