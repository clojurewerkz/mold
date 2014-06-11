(ns clojurewerkz.mold.services
  (:require [clojurewerkz.mold.internal.conversion :as cnv]
            [clojurewerkz.mold.internal.http       :as mhttp]
            [cheshire.core                         :as json])
  (:import clojurewerkz.mold.client.CFClient
           java.util.UUID))


;;
;; API
;;

(defn list-services
  [^CFClient client]
  (map cnv/service-offering->map (.. client impl getServiceOfferings)))

(defn list-service-plans
  [^CFClient client]
  (flatten (map :plans (list-services client))))

(defn list-instances
  [^CFClient client]
  (map cnv/service->map (.. client impl getServices)))

(defn find-service-by-unique-id
  [^CFClient client ^String id]
  (some (fn [m]
          (when (= id (:unique_id m))
            m))
        (list-services client)))

(defn find-service-plan-by-unique-id
  [^CFClient client ^String id]
  (some (fn [m]
          (when (= id (:unique_id m))
            m))
        (list-service-plans client)))

(defn ^UUID find-service-plan-guid-by-unique-id
  [^CFClient client ^String id]
  (:uuid (find-service-plan-by-unique-id client id)))

(defn ^UUID find-service-guid-by-unique-id
  [^CFClient client ^String id]
  (:uuid (find-service-by-unique-id client id)))

(defn migrate-service-instances
  [^CFClient client v1-guid v2-guid]
  (let [{:keys [body]} (mhttp/put client (format "/v2/service_plans/%s/service_instances" v1-guid)
                                 {:body {"service_plan_guid" v2-guid}})]
       (json/parse-string body)))

(defn purge-service-by-guid
  [^CFClient client guid]
  (let [{:keys [body]} (mhttp/delete client (format "/v2/services/%s" guid)
                                 {:query-params {"purge" true}})]
       (json/parse-string body)))
