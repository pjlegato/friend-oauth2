(ns friend-oauth2.metadata-test
  (:use
   midje.sweet
   friend-oauth2.fixtures)
  (:require  [friend-oauth2.metadata :as metadata]))

(fact "access-token-parse-fn extracts the entire access token"
      (metadata/access-token-parsefn access-token-response-fixture) => {:access_token "my-access-token"
                                                                        :id_token "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"})


(fact "extract-user-metadata extracts user metadata properly"
      ;; Note that the exact set of metadata returned is
      ;; provider-specific and subject to change.
      (metadata/extract-user-metadata 
       {:access-token
        (metadata/access-token-parsefn access-token-response-fixture)}) => {:exp 1300819380
                                                                            :iss "joe"
                                                                            :http://example.com/is_root true})
