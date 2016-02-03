(ns orchid.orchid-test
  (:require [orchid.core :refer [defapi GET]]))


(defapi
  (GET "/" [] "hi there!"))


;; TODO
;; (defapi
;;   (MIDDLEWARE (fn [] dostuff))
;;   (GET "/" [] "hi there!"))



