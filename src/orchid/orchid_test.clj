(ns orchid.orchid-test
  (:require [orchid.core :refer [def-orchid GET json-response grow]]))


(def-orchid
   (GET "/" [] "hi")
   (GET "/json" [] (json-response {:foo [:bar :baz]})))

(defn -main []
  (grow 8080))
