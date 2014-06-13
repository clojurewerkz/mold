(ns clojurewerkz.mold.internal.http
  (:refer-clojure :exclude [get])
  (:require clojurewerkz.mold.client
            [clj-http.client :as hc]
            [cheshire.core   :as json])
  (:import clojurewerkz.mold.client.CFClient
           org.cloudfoundry.client.lib.oauth2.OauthClient))

(def ^:const authorization-header "Authorization")

;;
;; Implementation
;;

(defn- ^String url-for
  [^CFClient client ^String path]
  (str (.url client) path))

(defn- ^OauthClient oauth-client-from
  [^CFClient client]
  (doto (.oauth-client client)
    ;; refreshes the token if needed
    (.getToken)))

(defn- inject-headers
  [opts oac]
  (-> opts
      (assoc-in [:headers authorization-header]
                (.getAuthorizationHeader oac))
      (assoc :throw-exceptions true)))

;;
;; API
;;

(defn get
  ([^CFClient client ^String path]
     (get client path {}))
  ([^CFClient client ^String path opts]
     (let [url   (url-for client path)
           oac   (oauth-client-from client)]
       (hc/get url (inject-headers opts oac)))))

(defn get-resources
  ([^CFClient client ^String path]
     (get-resources client path {}))
  ([^CFClient client ^String path opts]
     (let [{:keys [body]} (get client path opts)
           m              (json/parse-string body)
           xs             (clojure.core/get m "resources")]
       (if-let [next-url (clojure.core/get m "next_url")]
         (lazy-cat xs (get-resources client next-url opts))
         xs))))

(defn post
  [^CFClient client ^String path {:keys [body] :as opts}]
  (let [url   (url-for client path)
        oac   (oauth-client-from client)]
    (hc/post url (-> (inject-headers opts oac)
                     (assoc :body (json/generate-string body))))))

(defn put
  [^CFClient client ^String path {:keys [body] :as opts}]
  (let [url   (url-for client path)
        oac   (oauth-client-from client)]
    (hc/put url (-> (inject-headers opts oac)
                    (assoc :body (json/generate-string body))))))

(defn delete
  [^CFClient client ^String path opts]
  (let [url   (url-for client path)
        oac   (oauth-client-from client)]
    (hc/delete url (inject-headers opts oac))))
