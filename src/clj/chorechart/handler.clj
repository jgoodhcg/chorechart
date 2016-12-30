(ns chorechart.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [chorechart.layout :refer [error-page]]
            [chorechart.routes.definition :refer [auth-routes
                                            home-routes
                                            post-routes
                                            failed-authentication-handler]]
            [compojure.route :as route]
            [chorechart.env :refer [defaults]]
            [mount.core :as mount]
            [chorechart.middleware :as middleware]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(defn unauthorized-handler
  [request metadata]
  (str "unauthorized " request ";;;" metadata))
;; (cond
;;   ;; If request is authenticated, raise 403 instead
;;   ;; of 401 (because user is authenticated but permission
;;   ;; denied is raised).
;;   (authenticated? request)
;;   (-> (render (slurp (io/resource "error.html")) request)
;;       (assoc :status 403))
;;   ;; In other cases, redirect the user to login page.
;;   :else
;;   (let [current-url (:uri request)]
;;     (redirect (format "/login?next=%s" current-url)))))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))

(defn wrap-authenticate [handler]
  (restrict handler {:handler authenticated?
                     :on-error failed-authentication-handler}))

(def app-routes
  (routes
   (-> #'auth-routes
       (wrap-routes middleware/wrap-csrf))
   (-> #'home-routes
       (wrap-routes wrap-authentication auth-backend) ;; adds ability to authenticate
       (wrap-routes wrap-authenticate) ;; actually authenticates with (:identity)
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (-> #'post-routes
       (wrap-routes wrap-authentication auth-backend)
       (wrap-routes wrap-authenticate)
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
