(ns chorechart.resty.auth
  (:require
   [ring.util.http-response :as response]
   [chorechart.db.core :refer [*db*] :as db]
   [chorechart.routes.home :refer [signup-page login-page]]))

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
      (catch Exception e (login-page {:flash "failed login"})))))

