(ns orchid.middleware
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :as json]
            [cognitect.transit :as transit]
            [ring.util.json-response :refer [json-response]]
            [orchid.util :as util])
  (:import [java.lang.IllegalStateException]
           [java.io ByteArrayOutputStream]))

(def ^:dynamic *request-id* nil)
(defn request-id-middleware
  "Set a dynamic var to some unique ID so we can track a request."
  [app]
  (fn [request]
    (with-bindings {#'*request-id*
                    (->> (java.util.UUID/randomUUID)
                         str
                         (take 4)
                         (apply str))}

      (app request))))

(defn json-body-middleware [app]
  (fn [request]
    (if (and
         (not (nil? (:body request)))
         (util/request-is-json? request))
      (app (update-in request [:body] #(json/parse-string (slurp %) true)))
      (app request))))

(defn transit-write [x]
  (let [baos (ByteArrayOutputStream.)
        w    (transit/writer baos :json)
        _    (transit/write w x)
        ret  (.toString baos)]
    (.reset baos)
    ret))

(def transit-type (atom :json))

(defn handle-col-response [response]
  (case @transit-type
    :json
    (-> response
        (assoc :headers {"Content-Type" "application/json"
                         "Access-Control-Allow-Origin" "*"})
        (assoc :body (-> response :body json/encode)))

    :transit
    (-> response
        (assoc :headers {"Content-Type" "application/transit+json"
                         "Access-Control-Allow-Origin" "*"})
        (assoc :body (-> response :body transit-write)))))

(defn json-response-middleware [app]
  (fn [request]

    (let [response (app request)]
      (cond

        (-> response :body coll?)
        (handle-col-response response)

        (-> response :body string?)
        (-> response
            (assoc :headers {"Access-Control-Allow-Origin" "*"})
            (assoc :headers {"Content-Type" "text/html"}))

        :default
        response))))

(defn exception->status [e] ;; TODO protocol or multimethod?

  (cond (instance? IllegalStateException e) 403
        :else 500))

(defn exception-middleware
  [app config]
  (fn [request]
    (try
      (app request)
      (catch Exception e
        (timbre/error e "exception bubbled up too far")
        (if (:dev config)
          {:status (exception->status e)
           :body (str (type e) " " (.getMessage e))}
          {:status (exception->status e)
           :body "Oops! Something went terribly wrong!"})))))
