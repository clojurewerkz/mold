(ns clojurewerkz.mold.conversion
  (:import [org.cloudfoundry.client.lib CloudCredentials
                                        HttpProxyConfiguration]))



;;
;; API
;;

(defn ^CloudCredentials ->credentials
  [{:keys [^String email ^String password ^String client-id ^String client-secret]}]
  (if (and client-id client-secret)
    (CloudCredentials. email password client-id client-secret)
    (if client-id
      (CloudCredentials. email password client-id)
      (CloudCredentials. email password))))

(defn ^HttpProxyConfiguration ->http-proxy-configuration
  [{:keys [^String host port]}]
  (when (and host port)
    (HttpProxyConfiguration. host port)))
