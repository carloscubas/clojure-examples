(ns conta-corrente.core-test
  (:require [clojure.test :refer :all]
            [conta-corrente.core :refer :all]))

(deftest test-deposito
  (let [conta (criar-conta 1000)]
    (is (= 1200 (:saldo (depositar conta 200))))))

(deftest test-saque
  (let [conta (criar-conta 1000)]
    (is (= 500 (:saldo (sacar conta 500))))))

(deftest test-saque-insuficiente
  (let [conta (criar-conta 1000)]
    (is (= 1000 (get (sacar conta 2000) :saldo)))))

(run-tests)
