(ns orchid.test-utils
  (:require [ring.mock.request :as ring]
            [orchid.core :as core]
            [cheshire.core :as json]))

(defn ok?
  "Returns whether or not a response has a 200 response code."
  [response]
  (= 200 (:status response)))

(defn json?
  "Returns whether or not a response has application/json as the Content-Type."
  [response]
  (= (get-in response [:headers "Content-Type"]) "application/json"))

(defn- build-body
  [request body]
  (cond
    (nil? body)                      request
    (= java.lang.String (type body)) (ring/body request body)
    :else                            (-> request
                                         (ring/content-type "application/json")
                                         (ring/body (json/generate-string body)))))

(defn- build-request
  [method route body]
  (build-body (ring/request method route) body))

(defn- build-handler
  [app]
  (let [dev-middleware (core/middleware {:dev true})
        handler (dev-middleware app)]
    handler))

(defn- handle-response
  [response]
  (if (json? response)
    (update-in response [:body] json/parse-string)
    response))

(defn mock-request
  "Perform a mock request with an optional body. The body can be a string or a hashmap (indicating JSON request)."
  ([app method route body] (handle-response ((build-handler app) (build-request method route body))))
  ([app method route]      (handle-response ((build-handler app) (build-request method route nil)))))
