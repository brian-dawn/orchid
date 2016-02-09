(ns orchid.sample-app-test
  (:require  [clojure.test :refer :all]
             [orchid.test-utils :refer :all]
             [orchid.core :refer :all]
             [cheshire.core :as json]
             ))


(defroutes app

  (GET "/" [] "hello world!")
  (GET "/exception" [] (throw (new Exception "TestException")))
  (GET "/queryparams" [param] (json-response param))
  (GET "/urlparams/:param" [param] (json-response param))
  (POST "/postbody" {body :body} (json-response body)))

(deftest test-app

  (testing "base non json route"

    (let [response (mock-request app :get "/")]
      (is (ok? response))
      (is (not (json? response)))
      (is (= "hello world!" (:body response)))))

  (testing "url params"
    (let [response (mock-request app :get "/urlparams/foo")]
      (is (ok? response))
      (is (json? response))
      (is (= "foo" (:body response)))))
  
  (testing "query params"

    (let [response (mock-request app :get "/queryparams?param=foo")]
      (is (ok? response))
      (is (json? response))
      (is (= "foo" (:body response))))

    (let [response (mock-request app :get "/queryparams?param=foo&param=bar")]
      (is (ok? response))
      (is (json? response))
      (is (= ["foo" "bar"] (:body response)))))

  (testing "exception handling"
    (let [response (mock-request app :get "/exception")]
      (is (= 500 (:status response)))
      (is (.contains (:body response) "TestException"))))
  
  (testing "post body"
    (let [response (mock-request app :post "/postbody" {"foo" "bar"})]
      (is (ok? response))
      (is (json? response))
      (is (= {"foo" "bar"} (:body response))))))
