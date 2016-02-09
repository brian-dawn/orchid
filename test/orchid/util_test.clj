(ns orchid.util-test
  (:require [orchid.util :refer :all]
            [clojure.test :refer :all]))

(deftest test-utils
  (testing "update-keys-with-fn"
    (is (= {"foo" "BAR"
            "baz" "FOO"}
           (update-keys-with-fn {"FOO" "BAR"
                                 "bAz" "FOO"} clojure.string/lower-case))))

  (testing "request-is-json?"
    (is (not (request-is-json? {})))
    (is (not (request-is-json? {:headers {}})))
    (is (request-is-json? {:headers {"content-type" "application/json"}}))
    (is (request-is-json? {:headers {"CONTENT-TYPE" "APPLICATION/JSON"}}))
    (is (request-is-json? {:headers {"CoNTEnt-TyPE" "ApPlIcAtIoN/jSoN"}}))))
