(ns orchid.logging
  (:require
   [potemkin.namespaces :as potemkin]
   [taoensso.timbre :as timbre]))

(potemkin/import-vars [taoensso.timbre
                       info
                       warn
                       debug
                       error
                       color-str])
