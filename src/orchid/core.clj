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

            [puget.printer :as puget]

            [taoensso.timbre :as timbre]
            [aleph.http :as aleph]))

(potemkin/import-vars [compojure.core
                       defroutes
                       routes])

(potemkin/import-vars [compojure.route
                       not-found])

(potemkin/import-vars [ring.util.json-response
                       json-response])

(defn log-request [thing]
  (timbre/info ">" thing)
  thing)
(defn log-response [& thing]
  (timbre/info "<" thing)
  thing)

(defn make-route-macro [route-type path args & body]
  (compojure.core/compile-route
   route-type
   path
   args
   `((timbre/info "request" (str
                             "\n"
                             (puget/cprint-str
                              (into (sorted-map-by (fn [a# b#]
                                                     (cond
                                                       (= a# :id) -1
                                                       (= b# :id) 1

                                                       (= a# :type) -1
                                                       (= b# :type) 1

                                                       (= a# :route) -1
                                                       (= b# :route) 1

                                                       (= a# :params) -1
                                                       (= b# :params) 1

                                                       :else 0)))
                                    {:params ~args
                                     :type :get
                                     :id middleware/*request-id*
                                     :route ~path})

                              ;; We want to sort them ourselves.
                              {:sort-keys false})))
     (try
       (let* [response# (do ~@body)]

             (timbre/info (str
                           "\n"
                           (puget/cprint-str
                            {:status 200
                             :id middleware/*request-id*
                             :body response#})))
             {:body response#})
       (catch Exception e#
         (timbre/error (str "< " (middleware/exception->status e#) "\n" e#))
         ;; Pass exception to middleware.
         (throw e#)))))) ;; TODO what the shit is this?

(defmacro GET [path args & body]
  (apply make-route-macro :get path args body))

(defmacro PUT [path args & body]
  (apply make-route-macro :put path args body))

(defmacro POST [path args & body]
  (apply make-route-macro :post path args body))

(defmacro DELETE [path args & body]
  (apply make-route-macro :delete path args body))

(defn middleware [config]
  (let [base-middleware (fn [handler]
                          (-> handler
                              (wrap-defaults api-defaults)
                              middleware/request-id-middleware
                              middleware/json-body-middleware
                              middleware/json-response-middleware
                              (middleware/exception-middleware config)))]
    (if (:dev config)

      (fn [handler]
        (-> handler
            wrap-reload
            base-middleware))
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
