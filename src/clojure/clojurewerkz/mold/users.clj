(ns clojurewerkz.mold.users
  (:require [clojurewerkz.mold.internal.conversion :as cnv])
  (:import clojurewerkz.mold.client.CFClient
           [org.springframework.security.oauth2.common OAuth2AccessToken]))


;;
;; API
;;

(defn register
  [^CFClient client ^String email ^String password]
  (-> client .impl (.register email password)))

(defn ^OAuth2AccessToken raw-login
  [^CFClient client]
  (.. client impl login))

(defn login
  [^CFClient client]
  (cnv/oauth-access-token->map (.. client impl login)))

(defn logout
  [^CFClient client]
  (.. client impl logout))

