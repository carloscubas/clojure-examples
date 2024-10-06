(ns conta-corrente-api.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(def transactions (atom []))

; {:id 1 :value 1000.0 :type "credit"}
; {:id 1 :value 1000.0 :type "debit"}
(defn transaction [request]
  (let [transaction (:body request)]
    (swap! transactions conj transaction)
    {:status 201
     :body transaction}))

(defn account-balance [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))]
    (if (some #(= id (:id %)) @transactions)
      (let [balance (reduce (fn [acc transaction]
                              (cond
                                (and (= 1 (:id transaction)) (= "credit" (:type transaction))) (+ acc (:value transaction))
                                (and (= 1 (:id transaction)) (= "debit" (:type transaction))) (- acc (:value transaction))
                                :else acc))
                            0 @transactions)]
        {:status 200
         :body   {:balance balance}})
      {:status 404
       :body   {:error "Account not found"}})))

(defroutes app-routes
           (GET "/balance/:id" [] account-balance)
           (POST "/transaction" [] transaction)
           (route/not-found "Not Found"))

(def app
  (wrap-defaults
   (wrap-json-response
    (wrap-json-body app-routes {:keywords? true}))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defn -main [& args]
  (run-jetty app {:port 3000 :join? false}))
