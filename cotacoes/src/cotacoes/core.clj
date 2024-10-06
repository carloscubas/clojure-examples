(ns cotacoes.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def api-url "https://v6.exchangerate-api.com/v6/3d53166c17e960c20d3de228/latest/")

(defn get-quote [currency]
  "Busca a cotação da moeda especificada em relação ao USD."
  (let [response (client/get (str api-url currency)
                             {:headers {"Content-Type" "application/json"}})
        body (json/parse-string (:body response) true)]
    (if (= 200 (:status response))
      (:conversion_rates body)
      (str "Erro ao buscar cotação: " (:status response)))))

(defn get-rate [from to]
  "Converte um valor de uma moeda para outra"
  (let [rates (get-quote from)]
    (if (string? rates)
      rates
      (get rates (keyword to)))))

(defn convert [from to amount]
  "Converte um valor de uma moeda para outra."
  (let [rate (get-rate from to)]
    (if (number? rate)
      (* rate amount)
      rate)))

(defn -main
  [& args]
  (if (< (count args) 3)
    (println "Uso: <moeda-de-origem> <moeda-de-destino> <quantidade>")
    (let [from (nth args 0)
          to (nth args 1)
          amount (try
                   (Double/parseDouble (nth args 2))
                   (catch Exception e
                     (println "Quantidade inválida, usando 0.") 0.0))]
      (println (str "Convertendo " amount " de " from " para " to ":"))
      (println (convert from to amount)))))
