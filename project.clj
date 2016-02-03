(defproject orchid "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main orchid.orchid-test
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-json-response "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.4.0"]
                 [prone "0.8.2"]
                 [hiccup "1.0.5"]
                 [environ "1.0.1"]
                 [cheshire "5.5.0"]

                 [potemkin "0.4.1"]
                 [com.taoensso/timbre "4.2.1"]
                 ])
