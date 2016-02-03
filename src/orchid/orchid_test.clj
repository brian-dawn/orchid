(ns orchid.orchid-test
  (:require [orchid.core :refer [defapi GET json-response run]]))


(defapi
   (GET "/" [] "hi theres! wee")
   (GET "/json" [] (json-response {:foo [:bar :baz]})))

(defn -main []
  (run 8080))
