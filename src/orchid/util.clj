(ns orchid.util)

(defn update-keys-with-fn
  "Returns a map with f applied to all the keys."
  [mp f]
  (let [modified (map #(vector
                        (f (first %))
                        (second %))
                      (seq mp))]
    (->> modified
         flatten
         (apply hash-map))))

(defn request-is-json?
  "Checks the content-type in the header of the request and determines if json."
  [request]
  (let [modified-headers (update-keys-with-fn (:headers request) clojure.string/lower-case)]
    (= (clojure.string/lower-case (modified-headers "content-type"))
       "application/json")))




