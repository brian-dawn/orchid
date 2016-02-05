(ns orchid.sample-app
  (:require [orchid.core :refer [GET POST json-response grow info defroutes]]))

(defroutes app
  (GET "/" [] (json-response {:message "hello world!"}))

  (GET "/logme" [] (do (info "logging stuff!") "we logged some things"))

  ;; Will behave differently between dev/non dev.
  (GET "/exception" [] (throw (new Exception "EXCEPTION!!!!")))

  ;; JSON response.
  (GET "/json" [] (json-response {:foo [:bar :baz]}))
  
  ;; URL parameters.
  (GET "/hello/:name" [name] (str "hi " name))

  (GET "/sleep" [] (Thread/sleep 100000000000))

  ;; TODO test returning a manifold deferred
  ;; as indicated https://github.com/ztellman/aleph
  ;; also performance testing with jetty would be sweet!!!
  
  ;; Query parameters.
  ;; new idea deferreds that use cheshire streaming + manifold deferreds?
  (GET "/hello" [name] (str "hi " name))

  (POST "/jsonbody1" {body :body} (str "hi " (:name body)))
  (POST "/jsonbody2" {{:keys [name]} :body} (str "hi " name))
  ;; TODO documentation along with a cURL for how to trigger each.

  )

(defn -main []
  (grow app 8080))






