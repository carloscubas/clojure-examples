(ns rest-api.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(def tasks (atom []))

(defn get-tasks [_]
  {:status 200
   :body @tasks})

(defn add-task [request]
  (let [task (:body request)]
    (swap! tasks conj task)
    {:status 201
     :body task}))

(defn update-task [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        new-task (:body request)]
    (if (some #(= id (:id %)) @tasks)
      (do
        (swap! tasks (fn [tasks]
                       (map (fn [task]
                              (if (= id (:id task))
                                new-task
                                task))  tasks)))
        {:status 200
         :body new-task})
      {:status 404
       :body {:error "Task not found"}})))

(defn delete-task [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))]
    (if (some #(= id (:id %)) @tasks)
      (do
        (swap! tasks (fn [tasks] (remove #(= id (:id %)) tasks)))
        {:status 200
         :body {:message "Task deleted"}})
      {:status 404
       :body {:error "Task not found"}})))

(defroutes app-routes
           (GET "/tasks" [] get-tasks)
           (POST "/tasks" [] add-task)
           (PUT "/tasks/:id" [] update-task)
           (DELETE "/tasks/:id" [] delete-task)
           (route/not-found "Not Found"))

(def app
  (wrap-defaults
   (wrap-json-response
    (wrap-json-body app-routes {:keywords? true}))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defn -main [& args]
  (run-jetty app {:port 3000 :join? false}))
