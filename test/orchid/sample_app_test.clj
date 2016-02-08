(ns orchid.sample-app-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :refer :all]
             [orchid.test :refer :all]
             [orchid.core :refer :all]
             [cheshire.core :as json]
             ))


(defroutes app

  (GET "/" [] "hello world!")
  (GET "/exception" [] (new Exception "Test Exception"))
  (GET "/queryparams" [param] (json-response param))
  (GET "/urlparams/:param" [param] (json-response param))
  (POST "/postbody" {body :body} (json-response body)))



(defn ok? [response]
  (= 200 (:status response)))

(defn json? [response]
  (= (get-in response [:headers "Content-Type"]) "application/json"))

(defn mockrequest [app method route]
  (let [response (((middleware {:dev true}) app) (request method route))]
    (if (json? response)
      (update-in response [:body] json/parse-string)
      response)))

;; TODO cleanup or merge these methods.
(defn mockrequest-json-body [app method route json-body]
  (((middleware {:dev true}) app) (-> (request method route)
                            (body (json/generate-string json-body))
                            (content-type "application/json"))))

(deftest test-app

  (testing "base non json route"

    (let [response (mockrequest app :get "/")]
      (is (ok? response))
      (is (not (json? response)))
      (is (= "hello world!" (:body response)))))

  (testing "url params"
    (let [response (mockrequest app :get "/urlparams/foo")]
      (is (ok? response))
      (is (json? response))
      (is (= "foo" (:body response)))))
  
  (testing "query params"

    (let [response (mockrequest app :get "/queryparams?param=foo")]
      (is (ok? response))
      (is (json? response))
      (is (= "foo" (:body response))))

    (let [response (mockrequest app :get "/queryparams?param=foo&param=bar")]
      (is (ok? response))
      (is (json? response))
      (is (= ["foo" "bar"] (:body response)))))

  (testing "exception handling"
    (let [response (mockrequest app :get "/exception")]
      (is (= 500 (:status response)))
      ;; TODO check body
      ))
  
  (testing "post body"
    (let [response (mockrequest-json-body app :post "/postbody" {:foo "bar"})]
      (is (ok? response))
      (is (json? response))
      (is (= {:foo "bar"} (json/parse-string (:body response) true))))))
