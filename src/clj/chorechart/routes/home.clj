(ns chorechart.routes.home
  (:require [chorechart.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [chorechart.db.core :refer [*db*] :as db]
            [clojure.java.io :as io]
            [buddy.auth :refer [authenticated?]]
            ))

(defn home-page [req]
  (if-not (authenticated? req)
    (str "not authenticated " (authenticated? req) " " req)
    (layout/render "home.html")
    ))

(defn signup-page
  ([] (layout/render "signup.html"))
  ([ops] (layout/render "signup.html" ops)))

(defn login-page
  ([] (layout/render "login.html"))
  ([ops] (layout/render "login.html" ops)))

(defn signup [req]
  (let [{:keys [params]} req
        {:keys [user_name password confirm email]} params]
    (if (empty? user_name)
      (signup-page {:flash "username cannot be blank"})
      (if-not (= password confirm)
        (signup-page {:flash "passwords must match"})
        (try
          (do
            (db/add-person! {:user_name user_name :email email :password password})
            (response/found "/login"))
          (catch Exception e (signup-page {:flash "username taken"})))))))

(def authdata
  "TEST DATA TODO: REMOVE"
  {:user_name "test"
   :password "test"})

(defn login [req]
  ;; (str req))
  (let [{:keys [params]} req
        {:keys [user_name password]} params
        session (:session req)]
    (try
      (do
        (let [found-pass (:pass (db/find-person {:user_name user_name}))]
          (if (and found-pass (= found-pass password))
            (let [ updated-session (assoc session :identity user_name)]
              (-> (response/found "/")
                  (assoc :session updated-session)))
            (login-page {:flash "failed login"}))
          ))
      (catch Exception e (login-page {:flash "failed login"})))
    ))

(defroutes auth-routes
  (GET "/signup" [] (signup-page))
  (POST "/signup" [] signup)
  (GET "/login"  [] (login-page))
  (POST "/login" [] login))

(defroutes home-routes
  (GET "/" [] home-page)

  (POST "/add" []
    (fn [req]
      (let [chore (get-in req [:params :chore])
            name (get-in req [:params :name])
            date (get-in req [:params :date])]
        (str "" )
        )
      )
    )

  (GET "/docs" []
    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
        (response/header "Content-Type" "text/plain; charset=utf-8"))))


