(ns orchid.core-test
  (:require [clojure.test :refer :all]
            [orchid.core :refer :all]))

(defroutes app
  (GET "/" [] "hello world!"))


(grow app 8080)
(stop-server)
(grow app 8080 {:dev false})
(stop-server)

