(defproject orchid "0.2.0-SNAPSHOT"
  :description "A Clojure library to provide sane REST defaults for a compojure project."
  :url "https://github.com/brian-dawn/orchid"
  :main orchid.sample-app-with-real-server ;; Part of tests so isn't included in the actual library.
                                           ;; lein uberjar will complain but that's OK.
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ancient "0.6.10"]
            [lein-cljfmt "0.5.6"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.5.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-json-response "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.5.1"]
                 [cheshire "5.6.3"]
                 [aleph "0.4.1"]

                 [com.cognitect/transit-clj "0.8.297"]

                 [mvxcvi/puget "1.0.1"]

                 [ring/ring-mock "0.3.0"]

                 [potemkin "0.4.3"]
                 [com.taoensso/timbre "4.7.4"]
                 ])
