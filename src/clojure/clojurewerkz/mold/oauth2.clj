(ns clojurewerkz.mold.oauth2
  (:import [org.springframework.security.oauth2.common OAuth2AccessToken]))

;;
;; API
;;

(defn ^boolean expired?
  "Returns true if provided token has expired"
  [^OAuth2AccessToken token]
  (let [now (java.util.Date.)]
    (.after now (.getDate token))))
