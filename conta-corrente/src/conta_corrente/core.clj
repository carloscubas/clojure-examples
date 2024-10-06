(ns conta-corrente.core)

(defn criar-conta [saldo-inicial]
  "Cria uma nova conta com um saldo inicial."
  {:saldo saldo-inicial})

(defn depositar [conta valor]
  "Adiciona o valor ao saldo da conta."
  (update conta :saldo + valor))

(defn sacar [conta valor]
  "Saca um valor da conta, se houver saldo suficiente."
  (let [saldo-atual (:saldo conta)]
    (if (>= saldo-atual valor)
      (update conta :saldo - valor)
      (do (println "Saldo insuficiente!")
          conta))))

(defn mostrar-saldo [conta]
  "Mostra o saldo atual da conta."
  (println "Saldo atual:" (:saldo conta)))

(defn -main [& args]
  (let [conta (criar-conta 1000)]  ;; Saldo inicial de R$1000
    (mostrar-saldo conta)

    ;; Exemplo de depósito
    (println "\nDepositando R$200...")
    (let [conta-atualizada (depositar conta 200)]
      (mostrar-saldo conta-atualizada)

      ;; Exemplo de saque
      (println "\nSacando R$500...")
      (let [conta-atualizada (sacar conta-atualizada 500)]
        (mostrar-saldo conta-atualizada))

      ;; Tentativa de saque com saldo insuficiente
      (println "\nTentando sacar R$2000...")
      (let [conta-atualizada (sacar conta-atualizada 2000)]
        (mostrar-saldo conta-atualizada)))))