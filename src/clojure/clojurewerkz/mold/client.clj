(ns clojurewerkz.mold.client
  (:require [clojurewerkz.mold.internal.conversion :as cnv]
            [clojurewerkz.mold.oauth2     :as oa2]
            [clj-http.client :as http]
            [cheshire.core   :as json])
  (:import java.net.URL
           [org.cloudfoundry.client.lib CloudFoundryOperations CloudFoundryClient]
           org.cloudfoundry.client.lib.util.RestUtil
           [org.cloudfoundry.client.lib.rest LoggregatorClient CloudControllerClientImpl]
           org.cloudfoundry.client.lib.oauth2.OauthClient
           org.springframework.security.oauth2.common.OAuth2AccessToken))

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

(defn- ^String maybe-append
  [^String s suffix]
  (if (.endsWith s suffix)
    s
    (str s suffix)))


;;
;; API
;;

(defrecord CFClient
    [^CloudFoundryOperations impl
     ^String                 url
     ^OauthClient            oauth-client
     ^OAuth2AccessToken      oauth-token])

(defn info
  [^String url]
  (let [{:keys [body]} (http/get (maybe-append url "/v2/info") {:throw-exceptions true :accept :json})]
    (json/parse-string body false)))

(defn ^String get-authorization-endpoint
  "Infers and returns authorization (UAA API) endpoint used by
   the provided Cloud Controller URL."
  [^String url]
  (let [m              (info url)]
    (get m "authorization_endpoint")))

(defn ^clojurewerkz.mold.client.CFClient make-client
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
           proxy-cfg    (cnv/->http-proxy-configuration http-proxy-configuration)
           om           (org.codehaus.jackson.map.ObjectMapper.)
           ru           (RestUtil.)
           rt           (.createRestTemplate ru proxy-cfg trust-self-signed-certs?)
           oac          (oa2/make-oauth-client (get-authorization-endpoint url)
                                               trust-self-signed-certs?
                                               proxy-cfg)
           lc           (LoggregatorClient. trust-self-signed-certs?)
           ccci         (if (and (nil? organization)
                                 (nil? space))
                          (CloudControllerClientImpl. url'
                                                    rt oac lc
                                                    credentials' nil)
                          (CloudControllerClientImpl. url'
                                                    rt oac lc
                                                    credentials' organization space))
           cfc          (CloudFoundryClient. ccci)
           token        (.login cfc)]
       (CFClient. cfc url oac token))))
