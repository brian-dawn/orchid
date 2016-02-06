(defproject orchid "0.1.0-SNAPSHOT"
  :description "A Clojure library to provide sane REST defaults for a compojure project."
  :url "https://github.com/brian-dawn/orchid"
;  :main orchid.sample-app
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ancient "0.6.8"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-json-response "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.4.0"]
                 [prone "1.0.2"]
                 [hiccup "1.0.5"]
                 [environ "1.0.2"]
                 [cheshire "5.5.0"]
                 [aleph "0.4.1-beta2"]

                 [ring/ring-mock "0.3.0"]

                 [potemkin "0.4.3"]
                 [com.taoensso/timbre "4.2.1"]
                 ])
