(ns clojurewerkz.mold.client
  (:require [clojurewerkz.mold.conversion :as cnv])
  (:import java.net.URL
           [org.cloudfoundry.client.lib CloudFoundryOperations CloudFoundryClient]))

;;
;; Implementation
;;

(defprotocol URLSource
  (to-url [argument] "Produces a java.net.URL from the argument"))
(extend-protocol URLSource
  URL
  (to-url [arg]
    arg)

  String
  (to-url [arg]
    (URL. arg)))



;;
;; API
;;

(defn ^CloudFoundryOperations make-client
  ([url]
     (make-client url {}))
  ([url {:keys [credentials
                http-proxy-configuration
                trust-self-signed-certs?
                organization
                space] :or {credentials {}
                            trust-self-signed-certs? false}}]
     (let [url'         (to-url url)
           credentials' (cnv/->credentials credentials)
           proxy-cfg    (cnv/->http-proxy-configuration http-proxy-configuration)]
       (if (and (nil? organization) (nil? space))
         (CloudFoundryClient. credentials' url' proxy-cfg trust-self-signed-certs?)
         (CloudFoundryClient. credentials' url' organization space
                        proxy-cfg trust-self-signed-certs?)))))
