(ns chorechart.resty.auth
  (:require
   [ring.util.http-response :as response]
   [chorechart.db.core :refer [*db*] :as db]
   [chorechart.layout :as layout]
   [buddy.hashers :as hashers]
   [chorechart.misc :refer [mail]]
   ))

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
        {:keys [user_name password confirm email]} params
        flash (fn [msg] (signup-page {:flash msg}))]
    (cond
      (empty? email)             (flash "email cannot be blank")
      (nil?
       (re-matches
        #".+\@.+\..+" email))    (flash "please use a valid email address")
      (empty? user_name)         (flash "name cannot be blank")
      (empty? password)          (flash "password cannot be blank")
      (not (= password confirm)) (flash "passwords must match")
      :else (try
              (do
                (db/add-person!
                 {:user_name user_name :email email :password (hashers/derive password)})
                (mail
                 {:to email
                  :subject "Chorechart Signup"
                  :body "Thanks for signing up for Chorechart!"})
                (response/found "/login"))
              ;; the only thing that should break the above
              ;; is a taken email
              (catch Exception e
                (do (print (str (.getMessage e)))
                    (flash "there was an error, try another email"))
                )))))

(defn login [req]
  ;; (str req))
  (let [{:keys [params]} req
        {:keys [email password]} params
        session (:session req)]
    (try
      (do
        (let [person (db/find-person {:email email})
              found-pass (:pass person)]
          (if (and found-pass (hashers/check password found-pass))
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
