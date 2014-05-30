(ns friend-oauth2.metadata
  "Convenience functions that make it easier to extract metadata from an OAuth2 provider's reply.

  To extract user metadata such as the user's e-mail address while authenticating,
  create your workflow using the access-token-parsefn given here. For example:

    (oauth2/workflow {:client-config client-config
                      :uri-config uri-config
                      :access-token-parsefn  friend-oauth2.providers.metadata/access-token-parsefn
                      :credential-fn credential-fn})


  Within your credential-fn, you can call extract-user-metadata on the
  token passed into credential-fn to get a metadata map about the user.

  Note that the exact set of available metadata is
  provider-specific. Consult the provider's docs to see what metadata
  they make available.

   Using Google as an example
  provider (https://developers.google.com/accounts/docs/OAuth2Login#obtainuserinfo),
  we can do:

    (defn credential-fn
     \"Given an OAuth2 token, returns either a Friend identity map or nil.\"
     [token]
     (let [user-metadata (extract-user-metadata token)]

        ;; Do app-specific stuff with the user's data here, e.g.
        (println \"User's email is:\" (:email user-metadata))
        (println \"User's email verification status is:\" (:email_verified user-metadata))

        ;; Return a Friend identity map:
        {:identity (:email user-metadata)
         :oauth-token token ;; <- optional; keep the original token around in the 
                            ;;    identity map for later use elsewhere
         :roles #{::user}   ;; do something to look up the correct roles here
        }))
"
  (:require [clojure.string  :refer [split]]
            [cheshire.core   :refer [parse-string]]
            [ring.util.codec :refer [base64-decode]]))

(defn access-token-parsefn
  "Replacement for the default access-token-parsefn that 
  doesn't throw away the metadata sent by the provider. Give 
  this function as your :access-token-parsefn when you call
  friend-oauth2.workflow/workflow." 
  [token]
  (-> token :body (parse-string true)))



(defn extract-user-metadata
  "Given the reply token from a successful authentication,
   such as is passed to credential-fn, extracts and returns the user
   metadata map encoded in it.

   The exact information contained in this metadata map is
   provider-specific. As an example, Google's version is described at
   https://developers.google.com/accounts/docs/OAuth2Login#obtainuserinfo .

   In a basic authentication scenario, the user's e-mail and whether it
   is verified are probably the only interesting things there.

   Note that this function does NOT perform cryptographic verification
   of the JSON Web Token (JWT) encoded in the reply from the provider.

   If your code is communicating directly with the provider via HTTPS, the
   most common authorization scenario, this is fine. If you are engaging
   in more complex authentication scenarios, you will have to perform
   cryptographic verification of the JWT yourself. See the Google documentation
   above for further information on this."
  [token]
  (let [[header payload signature] (split (-> token :access-token :id_token) #"\.")
        payload-json               (String. (base64-decode payload) "UTF-8")]
    (parse-string payload-json true)))
