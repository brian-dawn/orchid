(ns orchid.core
  (:require [orchid.util :as util]
            [orchid.middleware :as middleware]
            [potemkin.namespaces :as potemkin]
            [compojure.core]
            [compojure.route]

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

(potemkin/import-vars [compojure.route
                       not-found])

(potemkin/import-vars [ring.util.json-response
                       json-response])

(def middleware (fn [handler] (-> handler
                                  (wrap-defaults api-defaults)
                                  middleware/json-body-middleware
                                  )))

(def middleware-dev (fn [handler] (-> handler
                                      middleware/logging-request-middleware
                                      wrap-reload
                                      middleware
                                      middleware/exception-middleware ;; TODO this should go in non dev middleware. We still want to 500 response on an exception we just don't want to display the exception.
                                      middleware/logging-response-middleware
                                      )))


(defonce running-server (atom nil))

(defn stop-server []
  (when (not (nil? @running-server))
    (do
      (timbre/info "stopping webserver")
      (try
        (.close ^java.io.Closeable @running-server)
        (catch Exception e)))))

(defn start-server [routes port]
  (when (not (nil? @running-server))
    (stop-server))
  (do
    (reset! running-server (aleph/start-server routes {:port port}))
    (timbre/info (timbre/color-str :green "orchid is ready 🌺")))) ;; TODO need to make message have higher corn content if possible.)))

(defmacro grow
  "Convenience macro for starting a server with the routes passed as a var."
  [app port]
  `(start-server (middleware-dev (var ~app)) ~port))
