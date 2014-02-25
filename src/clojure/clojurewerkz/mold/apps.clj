(ns clojurewerkz.mold.apps
  (:import [org.cloudfoundry.client.lib CloudFoundryOperations]
           java.util.UUID))

;;
;; API
;;

(defn list
  [^CloudFoundryOperations client]
  (.getApplications client))

(defn by-name
  [^CloudFoundryOperations client ^String name]
  (.getApplication client name))

(defn by-uuid
  [^CloudFoundryOperations client ^UUID id]
  (.getApplication client id))
