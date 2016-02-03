(ns orchid.core
  (:require [potemkin.namespaces :as potemkin]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core]
            [compojure.route :refer [not-found resources]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [cheshire.core :refer :all]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.util.json-response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.tools.macro :as macro]

            [taoensso.timbre :as timbre]
   )
  )

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

(def middleware (fn [handler] (-> handler
                                  (wrap-defaults api-defaults)
                                  wrap-json-body
                                  )))

(def middleware-dev (fn [handler] (-> handler
                                      wrap-exceptions
                                      wrap-reload
                                      middleware
                                      )))



(defonce running-server (atom nil))

(defn start-jetty [routes port]
  (timbre/info (timbre/color-str :yellow "applying sunlight"))
  (reset! running-server
          (run-jetty (middleware-dev routes) {:port port :join? false})))

(defn start-server [routes port]
  (when (nil? @running-server)
    (start-jetty routes port)))

(defmacro def-orchid
  "Define a Ring handler function from a sequence of routes. The name may
  optionally be followed by a doc-string and metadata map."
  [& routes]
  (let [[name routes] (macro/name-with-attributes 'orchid-api-routes routes)]
    `(def ~name (routes ~@routes))))

(defmacro grow [port]
  `(start-server ~'(var orchid-api-routes) ~port))
