(ns clojurewerkz.mold.services
  (:require [clojurewerkz.mold.conversion :as cnv])
  (:import [org.cloudfoundry.client.lib CloudFoundryOperations]
           java.util.UUID))


;;
;; API
;;

(defn list-services
  [^CloudFoundryOperations client]
  (map cnv/service-offering->map (.getServiceOfferings client)))

(defn list-service-plans
  [^CloudFoundryOperations client]
  (flatten (map :plans (list-services client))))

(defn list-instances
  [^CloudFoundryOperations client]
  (map cnv/service->map (.getServices client)))

(defn find-service-plan-by-unique-id
  [^CloudFoundryOperations client ^String id]
  (some (fn [m]
          (when (= id (:unique_id m))
            m))
        (list-service-plans client)))
