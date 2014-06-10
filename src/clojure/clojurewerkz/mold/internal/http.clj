(ns clojurewerkz.mold.internal.http
  (:refer-clojure :exclude [get])
  (:require clojurewerkz.mold.client
            [clj-http.client :as hc]
            [cheshire.core   :as json])
  (:import clojurewerkz.mold.client.CFClient))

(def ^:const authorization-header "Authorization")

;;
;; API
;;

;; TODO: remove structural duplication

(defn get
  [^CFClient client ^String path opts]
  (let [url   (str (.url client) path)
        oac   (.oauth-client client)]
    ;; refreshes the token if needed.
    (.getToken oac)
    (hc/get url (-> opts
                    (assoc-in [:headers authorization-header]
                              (.getAuthorizationHeader oac))
                    (assoc :throw-exceptions true)))))

(defn post
  [^CFClient client ^String path {:keys [body] :as opts}]
  (let [url   (str (.url client) path)
        oac   (.oauth-client client)]
    (.getToken oac)
    (hc/post url (-> opts
                     (assoc-in [:headers authorization-header]
                               (.getAuthorizationHeader oac))
                     (assoc :body (json/generate-string body))
                     (assoc :throw-exceptions true)))))

(defn put
  [^CFClient client ^String path {:keys [body] :as opts}]
  (let [url   (str (.url client) path)
        oac   (.oauth-client client)]
    (.getToken oac)
    (hc/put url (-> opts
                    (assoc-in [:headers authorization-header]
                              (.getAuthorizationHeader oac))
                    (assoc :body (json/generate-string body))
                    (assoc :throw-exceptions true)))))
