(ns orchid.middleware
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :as json]
            [orchid.util :as util]))

(defn colorize-request-method
  "Colorize a keyword corresponding to a request method and convert it to a non keyword."
  [method]
  (let [color (case method
                :get :green
                :put :yellow
                :delete :red
                :post :blue
                :white)]
    (timbre/color-str color (name method))))

(defn colorize-status [status]
  (let [color (cond
                (<= 200 status 299) :purple
                (<= 300 status 399) :blue
                (<= 400 status 499) :yellow
                (<= 500 status)     :red
                :else               :white)]
    (timbre/color-str color (str status))))

(defn json-body-middleware [app]
  (fn [request]
    (if (and
         (not (nil? (:body request)))
         (util/request-is-json? request))
      (app (update-in request [:body] #(json/parse-string (slurp %) true)))
      (app request))))

(defn logging-request-middleware
  "Middleware that logs each request made. This MUST be first in the middleware
   list so we can rip off the namespace from the app."
  [app]
  (fn [request]
    (timbre/info ">"
                 (colorize-request-method (:request-method request))
                 (:uri request)
                 (str "params=" (:params request))
                 (str (when (:body request) (str "body=" (:body request)))))
    (app request)))

(defn logging-response-middleware
  "Middleware that logs each response made."
  [app]
  (fn [request]
    (let [response (app request)]
      (timbre/info "<"
                   (colorize-status (:status response)))
      response)))

(defn exception-middleware
  [app config]
  (fn [request]
    (try
      (app request)
      (catch Exception e
        (timbre/error e "exception bubbled up too far")
        (if (:dev config)
          {:status 500
           :body (str (type e) " " (.getMessage e))}
          {:status 500
           :body "Oops! Something went terribly wrong!"})))))
