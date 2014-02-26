(ns clojurewerkz.mold.users
  (:require [clojurewerkz.mold.conversion :as cnv])
  (:import [org.cloudfoundry.client.lib CloudFoundryOperations]
           [org.springframework.security.oauth2.common OAuth2AccessToken]))


;;
;; API
;;

(defn register
  [^CloudFoundryOperations client ^String email ^String password]
  (.register client email password))

(defn ^OAuth2AccessToken raw-login
  [^CloudFoundryOperations client]
  (.login client))

(defn login
  [^CloudFoundryOperations client]
  (cnv/oauth-access-token->map (.login client)))

(defn logout
  [^CloudFoundryOperations client]
  (.logout client))

