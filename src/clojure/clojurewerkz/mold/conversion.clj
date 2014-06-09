(ns clojurewerkz.mold.conversion
  (:import [org.cloudfoundry.client.lib CloudCredentials
                                        HttpProxyConfiguration]
           [org.springframework.security.oauth2.common OAuth2AccessToken]
           [org.cloudfoundry.client.lib.domain CloudEntity CloudEntity$Meta
            CloudService CloudServiceOffering CloudServicePlan]
           java.util.UUID))



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

(defn oauth-access-token->map
  [^OAuth2AccessToken token]
  {:scope (set (.getScope token))
   :type (.getTokenType token)
   :expired? (.isExpired token)
   :expiration (.getExpiration token)
   :expires-in (.getExpiresIn token)
   :value (.getValue token)
   :additional-information (into {} (.getAdditionalInformation token))})

(defn meta->map
  [^CloudEntity$Meta m]
  {:created (.getCreated m)
   :updated (.getUpdated m)
   :uuid    (.getGuid    m)})

(defn cloud-entity->map
  [^CloudEntity ce]
  {:meta (meta->map (.getMeta ce))
   :name (.getName ce)})

(declare uuid-from)

(defn service->map
  [^CloudService cs]
  (let [ce (cloud-entity->map cs)]
    (merge ce {:is-user-provided? (.isUserProvided cs)
               :version           (.getVersion cs)
               :provider          (.getProvider cs)
               :plan              (.getPlan cs)
               :uuid              (get-in ce [:meta :uuid])})))

(defn service-plan->map
  [^CloudServicePlan csp]
  (let [ce (cloud-entity->map csp)]
    (merge ce {:public? (.isPublic csp)
               :free?   (.isFree csp)
               :uuid        (get-in ce [:meta :uuid])
               :description (.getDescription csp)
               :unique_id   (.getUniqueId csp)
               :extra       (.getExtra csp)})))

(defn service-offering->map
  [^CloudServiceOffering cso]
  (let [ce (cloud-entity->map cso)]
    (merge ce {:provider (.getProvider cso)
               :version  (.getVersion cso)
               :label    (.getLabel cso)
               :uuid     (get-in ce [:meta :uuid])
               :description (.getDescription cso)
               :active?     (.isActive cso)
               :bindable?   (.isBindable cso)
               :unique_id   (.getUniqueId cso)
               :url         (.getUrl cso)
               :info-url    (.getInfoUrl cso)
               :extra       (.getExtra cso)
               ;; not available in the most recent release. MK.
               ;; :documentation-url (.getDocumentationUrl cso)
               :plans       (map service-plan->map (.getCloudServicePlans cso))})))

(defprotocol UUIDSource
  (uuid-from [arg] "Produces a UUID from input"))

(extend-protocol UUIDSource
  UUID
  (uuid-from [^UUID arg]
    arg)

  String
  (uuid-from [^String arg]
    (UUID/fromString arg)))

