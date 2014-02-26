(ns clojurewerkz.mold.oauth2
  (:import [org.springframework.security.oauth2.common OAuth2AccessToken]))

;;
;; API
;;

(defprotocol AccessToken
  (expired? [arguments] "Returns true if provided token has expired"))

(extend-protocol AccessToken
  OAuth2AccessToken
  (expired? [token]
    (let [now (java.util.Date.)]
      (.after now (.getExpiration token))))

  clojure.lang.IPersistentMap
  (expired? [token]
    (let [now (java.util.Date.)]
      (.after now (:expiration token)))))
