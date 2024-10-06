(ns conta-corrente-api.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(def accounts (atom []))

(defn add-new-account [request]
  (let [account (:body request)]
    (swap! accounts conj account)
    {:status 201
     :body account}))

(defn deposit [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        value (:body request)]
    (if (some #(= id (:id %)) @accounts)
      (let [account (first (filter #(= id (:id %)) @accounts))
            updated-balance (+ (get account :balance) (:value value))
            account-updated (assoc account :balance updated-balance)]
        (swap! account conj account-updated)
        {:status 200
         :body account-updated})
      {:status 404
       :body {:error "Account not found"}})))

(defn withdraw [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        withdraw (:body request)]
    {:status 200
     :body {}}))

(defn account-balance [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))]
    (if (some #(= id (:id %)) @accounts)
      (let [balance (get (first (filter #(= id (:id %)) @accounts)) :balance)]
        {:status 200
         :body   {:balance balance}})
      {:status 404
       :body {:error "Account not found"}})))

(defroutes app-routes
           (GET "/balance/:id" [] account-balance)
           (POST "/new" [] add-new-account)
           (PUT "/deposit/:id" [] deposit)
           (PUT "/withdraw/:id" [] withdraw)
           (route/not-found "Not Found"))

(def app
  (wrap-defaults
   (wrap-json-response
    (wrap-json-body app-routes {:keywords? true}))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defn -main [& args]
  (run-jetty app {:port 3000 :join? false}))
