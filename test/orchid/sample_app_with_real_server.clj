(ns orchid.sample-app-with-real-server
  (:require  [orchid.core :refer :all]))

;; This file isn't distributed with the library.

(defroutes app

  (GET "/" [] "hello world!")
  (GET "/exception" [] (throw (new Exception "TestException")))
  (GET "/queryparams" [param] param)
  (GET "/urlparams/:param" [param] param)
  (POST "/postbody" {body :body} body))

(defn -main [& args]
  (grow app 8080))
