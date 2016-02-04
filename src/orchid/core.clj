(ns orchid.core
  (:require [potemkin.namespaces :as potemkin]
            [environ.core :refer [env]]
            [compojure.core]
            [compojure.route :refer [not-found resources]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [cheshire.core :as json]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.util.json-response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.tools.macro :as macro]

            [taoensso.timbre :as timbre]
            [aleph.http :as aleph]))

(potemkin/import-vars [taoensso.timbre
                       info
                       warn
                       debug
                       error
                       color-str])

(potemkin/import-vars [compojure.core
                       defroutes
                       routes
                       GET
                       PUT
                       POST
                       DELETE])

(potemkin/import-vars [ring.util.json-response
                       json-response])


;; TODO test that we can have varying case on application/json and content-type.
(defn json-body-middleware [app]
  (fn [request]
    (if (and
         (not (nil? (:body request)))
         (= ((:headers request) "content-type") "application/json"))
      (app (update-in request [:body] #(json/parse-string (slurp %) true)))
      (app request))))


(def middleware (fn [handler] (-> handler
                                  (wrap-defaults api-defaults)
                                  json-body-middleware
                                  )))

(def middleware-dev (fn [handler] (-> handler
                                      wrap-exceptions
                                      wrap-reload
                                      middleware
                                      )))



(defonce running-server (atom nil))
(defonce routes (atom nil))

(defn start-server [routes port]
  (when (nil? @running-server)
    (do
      (timbre/info (timbre/color-str :yellow "the sun shines upon you 🌱  🌺")) ;; TODO need to make message have higher corn content if possible.
      (reset! running-server (aleph/start-server (middleware-dev routes) {:port port})))))

(defmacro grow [app port]
  `(start-server (var ~app) ~port))
