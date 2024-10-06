(defproject conta-corrente-api "0.1.0-SNAPSHOT"
  :description "A simple TODO API in Clojure"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.6.2"]
                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-json "0.5.0"]
                 [ring "1.1.6"]
                 [ring/ring-anti-forgery "1.3.1"]]
  :main ^:skip-aot conta-corrente-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
