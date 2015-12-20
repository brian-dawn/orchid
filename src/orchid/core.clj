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
   )
  )


(potemkin/import-vars [compojure.core
                       defroutes
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


(defonce running-servers (atom {}))

(defn start-server [app port]
  (when-let [old-server (get @running-servers port)]
    (when (.isRunning old-server)
      (.stop old-server)))
  (swap! running-servers merge {port (run-jetty app {:port port :join? false})}))

