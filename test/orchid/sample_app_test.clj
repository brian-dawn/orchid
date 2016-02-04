(ns orchid.sample-app-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :refer :all]
             [orchid.test :refer :all]
             [orchid.sample-app :refer :all]))

(defn mockrequest [app method route]
  (app (request method route)))

(mockrequest app :post "/jsonbody")

(deftest test-app

  (testing "testing stuff"
    ;; TODO we just have to store the current application routes in an atom :P gross I know but eh sweep it under the rug.
    (let [response (mockrequest app :get "/")]
      (println response)
      )))
