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
    ;; (str "not authenticated " (authenticated? req))
    (str req)
    (layout/render "home.html")
    ))

(defn signup-page []
  (layout/render "signup.html"))

(defn login-page []
  (layout/render "login.html"))

(defn signup [req]
  (let [{:keys [params]} req
        {:keys [username password confirm]} params]
    (str username " " password " " confirm)))

(def authdata
  "TEST DATA TODO: REMOVE"
  {:user-name "test"
   :password "test"})

(defn login [req]
  ;; (str req))
  (let [{:keys [params]} req
        {:keys [user-name password]} params
        session (:session req)
        found-pass (:password authdata)]
    (if (and found-pass (= found-pass password))
      (let [ updated-session (assoc session :identity user-name)]
        (-> (response/found "/")
            (assoc :session updated-session)))
      (str "failed login"))))

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
        (str (db/ins-chore! {:chore chore :person name :completed date}))
        )
      )
    )

  (GET "/docs" []
    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
        (response/header "Content-Type" "text/plain; charset=utf-8"))))


