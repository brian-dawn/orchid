(ns orchid.core
  (:require [potemkin.namespaces :as potemkin]
            [compojure.core]
            [compojure.route]
            [cheshire.core :as json]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.util.json-response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.tools.macro :as macro]

            [taoensso.timbre :as timbre]
            [aleph.http :as aleph]))

(potemkin/import-vars [prone.debug
                       debug])

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

(potemkin/import-vars [compojure.route
                       not-found])

(potemkin/import-vars [ring.util.json-response
                       json-response])

;; TODO dev request logger?

;; TODO test that we can have varying case on application/json and content-type.
(defn json-body-middleware [app]
  (fn [request]
    (if (and
         (not (nil? (:body request)))
         (= ((:headers request) "content-type") "application/json"))
      (app (update-in request [:body] #(json/parse-string (slurp %) true)))
      (app request))))

(defn colorize-request-method [method]
  (let [color (case method
                :get :green
                :put :yellow
                :delete :red
                :post :blue
                :white)]
    (timbre/color-str color (name method)))
  )

(defn colorize-status [status]
  (timbre/color-str :purple (str status))
  )

(defn logging-request-middleware [app]
  (fn [request]
    (timbre/info ">"
                 (colorize-request-method (:request-method request))
                 (:uri request)
                 (str "params=" (:params request))
                 (str "body=" (:body request)))
    (app request)
    )
  )

(defn logging-response-middleware [app]
  (fn [request]
    (let [response (app request)]
      (timbre/info "<"
                   (colorize-status (:status response))
                   ;;(str "body=" (:body response))
                   )
      response
      )
    )
  )

(def middleware (fn [handler] (-> handler
                                  (wrap-defaults api-defaults)
                                  json-body-middleware
                                  )))

(def middleware-dev (fn [handler] (-> handler
                                      logging-request-middleware
                                      wrap-exceptions
                                      wrap-reload
                                      middleware
                                      logging-response-middleware
                                      )))


(defonce running-server (atom nil))
(defonce routes (atom nil))

(defn start-server [routes port]
  (when (nil? @running-server)
    (do
      (timbre/info (timbre/color-str :green "orchid is ready 🌺")) ;; TODO need to make message have higher corn content if possible.
      (reset! running-server (aleph/start-server (middleware-dev routes) {:port port})))))

(defmacro grow [app port]
  `(start-server (var ~app) ~port))
