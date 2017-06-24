(ns chorechart.misc
  (:require
   [postal.core :as postal]
   [chorechart.config :refer [env]]))

(def connection
  {:host "smtp.gmail.com"
   :user "chorechart.hcg"
   :pass (env :mail-password)
   :ssl true})

(defn mail [{:keys [to subject body]}]
  (postal/send-message
   connection
   {:from "chorechart.hcg@gmail.com"
    :to to
    :subject subject
    :body body}) )
