(ns clojurewerkz.mold.users
  (:import [org.cloudfoundry.client.lib CloudFoundryOperations]))


;;
;; API
;;

(defn register
  [^CloudFoundryOperations client ^String email ^String password]
  (.register client email password))

(defn login
  [^CloudFoundryOperations client]
  (.login client))

(defn logout
  [^CloudFoundryOperations client]
  (.logout client))

