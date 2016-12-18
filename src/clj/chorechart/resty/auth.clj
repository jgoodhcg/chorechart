(ns chorechart.resty.auth
  (:require
   [ring.util.http-response :as response]
   [chorechart.db.core :refer [*db*] :as db]
   [chorechart.layout :as layout]))

(defn signup-page
  ([] (layout/render "signup.html"))
  ([ops] (layout/render "signup.html" ops)))

(defn login-page [error]
  (case error
    "" (layout/render "login.html")
    "failed" (layout/render "login.html" {:flash "failed login"})
    "no-auth" (layout/render "login.html" {:flash "please log in to do that"})))

(defn signup [req]
  (let [{:keys [params]} req
        {:keys [user_name password confirm email]} params]
    (if (empty? email)
      (signup-page {:flash "email cannot be blank"})
      (if-not (= password confirm)
        (signup-page {:flash "passwords must match"})
        (let [house_name (str user_name "'s house")] ;; default household name
          (try
            (do
              (db/add-person! {:user_name user_name :email email :password password})
              (response/found "/login"))
            ;; logically the only thing that breaks the above is taken person user_name
            (catch Exception e (signup-page {:flash (str (.getMessage e))}))))))))

(defn login [req]
  ;; (str req))
  (let [{:keys [params]} req
        {:keys [email password]} params
        session (:session req)]
    (try
      (do
        (let [person (db/find-person {:email email})
              found-pass (:pass person)]
          (if (and found-pass (= found-pass password))
            (let [ updated-session (assoc session
                                          :identity email
                                          :person (select-keys person [:id :user_name :email]))]
              (-> (response/found "/")
                  (assoc :session updated-session)))
            (response/found "/login/failed"))
          ))
      (catch Exception e (response/found "/login/failed")))))

(defn logout [req]
  (let [session (:session req)]
    (-> (response/found "/login")
        (assoc :session nil))))
