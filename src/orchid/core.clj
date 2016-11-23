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

(defn middleware [config]
  (let [base-middleware (fn [handler]
                          (-> handler
                              (wrap-defaults api-defaults)
                              middleware/json-body-middleware
                              (middleware/exception-middleware config)))]
    (if (:dev config)
      (fn [handler] (-> handler
                        middleware/logging-request-middleware
                        wrap-reload
                        base-middleware
                        middleware/logging-response-middleware))
      base-middleware)))

(defonce running-server (atom nil))

(defn stop-server []
  (when-not (nil? @running-server)
    (timbre/info "stopping webserver")
    (try
      (.close ^java.io.Closeable @running-server)
      (catch Exception e))))

(defn start-server [routes port]
  (when-not (nil? @running-server)
    (stop-server))
  (do
    (reset! running-server (aleph/start-server routes {:port port}))
    (timbre/info (timbre/color-str :green "orchid is ready 🌺")))) ;; TODO need to make message have higher corn content if possible.)))

(defmacro grow
  "Convenience macro for starting a server with the routes passed as a var."
  ([app port]
   `(start-server ((middleware {:dev true}) (var ~app)) ~port))
  ([app port config]
   `(start-server ((middleware ~config) (var ~app)) ~port)))
