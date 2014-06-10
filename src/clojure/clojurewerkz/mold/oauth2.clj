(ns clojurewerkz.mold.oauth2
  (:import org.cloudfoundry.client.lib.util.RestUtil
           org.cloudfoundry.client.lib.oauth2.OauthClient
           org.cloudfoundry.client.lib.HttpProxyConfiguration
           java.net.URL))

;;
;; API
;;

(defn ^OauthClient make-oauth-client
  ([^String url trust-self-signed-certs?]
     (make-oauth-client url trust-self-signed-certs? nil))
  ([^String url trust-self-signed-certs? ^HttpProxyConfiguration http-proxy-config]
     (let [ru (RestUtil.)]
       (.createOauthClient ru (URL. url)
                           http-proxy-config
                           trust-self-signed-certs?))))
