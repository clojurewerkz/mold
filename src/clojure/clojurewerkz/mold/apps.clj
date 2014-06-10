(ns clojurewerkz.mold.apps
  (:import clojurewerkz.mold.client.CFClient
           java.util.UUID))

;;
;; API
;;

(defn list
  [^CFClient client]
  (.. client impl getApplications))

(defn by-name
  [^CFClient client ^String name]
  (-> client .impl (.getApplication name)))

(defn by-uuid
  [^CFClient client ^UUID id]
  (-> client .impl (.getApplication id)))
