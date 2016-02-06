(ns orchid.core
  (:require [potemkin.namespaces :as potemkin]
            [compojure.core]
            [compojure.route]
            [cheshire.core :as json]
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

;; TODO test that we can have varying case on application/json and content-type.
(defn json-body-middleware [app]
  (fn [request]
    (if (and
         (not (nil? (:body request)))
         (= ((:headers request) "content-type") "application/json"))
      (app (update-in request [:body] #(json/parse-string (slurp %) true)))
      (app request))))

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
                (<= 200 status 299) :blue
                (<= 300 status 399) :purple
                (<= 400 status 499) :yellow
                (<= 500 status)     :red
                :else               :white)]
    (timbre/color-str color (str status))))

;; (defmacro change-ns-in-log
;;   "Because logging expands to a macro we're going to cheat a little.
;;    Expand the logging macro, do a replace on our namespace with whatever
;;    namespace we want."
;;   [new-ns form]
;;   (replace {(str (ns-name *ns*)) new-ns} (macroexpand form)))

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
  [app]
  (fn [request]
    (try
      (app request)
      (catch Exception e
        (timbre/error e "exception bubbled up too far")
        {:status 500
         :body (str (type e) " " (.getMessage e))} ;; TODO we should only return this in dev mode.
        ))))

(def middleware (fn [handler] (-> handler
                                  (wrap-defaults api-defaults)
                                  json-body-middleware
                                  )))

(def middleware-dev (fn [handler] (-> handler
                                      logging-request-middleware
                                      wrap-reload
                                      middleware
                                      exception-middleware
                                      logging-response-middleware
                                      )))


(defonce running-server (atom nil))

;; TODO test these next 3 methods... grow can be tested by using eval I think...
;; TODO websockets
;; TODO get the loggers to log the namespace of the routes. I think this should be possible by putting the middleware on the grow macro?
;; TODO to get the loggers to log the namespace of the routes implement our own GET macro.
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
