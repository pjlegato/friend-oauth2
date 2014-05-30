(ns friend-oauth2.fixtures
  (:require
   [ring.util.response :refer [response content-type]]
   [friend-oauth2.util :as oauth2-util]))

(def client-config-fixture
  {:client-id "my-client-id"
   :client-secret "my-client-secret"
   :callback {:domain "http://127.0.0.1" :path "/redirect"}})

(def uri-config-fixture
  {:authentication-uri {:url "http://example.com/authenticate"
                        :query {:client_id (:client-id client-config-fixture)
                                :redirect_uri (oauth2-util/format-config-uri client-config-fixture)}}

   :access-token-uri {:url "http://example.com/get-access-token"
                      :query {:client_id (client-config-fixture :client-id)
                              :client_secret (client-config-fixture :client-secret)
                              :redirect_uri (oauth2-util/format-config-uri client-config-fixture)}}})

;; http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-5.1
(def access-token-response-fixture
  (-> "{\"access_token\": \"my-access-token\", \"id_token\": \"eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk\"}"
      response
      (content-type "application/json")))

(def identity-fixture
  {:identity "my-access-token"
   :access_token "my-access-token"})


