# orchid

A Clojure library to provide sane REST defaults for a compojure project.

[![Clojars Project](http://clojars.org/orchid/latest-version.svg)](https://clojars.org/orchid)
[![Dependencies Status](https://jarkeeper.com/brian-dawn/orchid/status.svg)](https://jarkeeper.com/brian-dawn/orchid)

## Rationale

I felt that using setting up a Compojure application can be difficult for people
coming to Clojure from other ecosystems. Tracking down all the middleware to
enable automatic JSON conversion, setting up auto-reloading/server start from the REPL,
getting prone to aid with debugging will quickly test peoples patience.

Orchid aims to just be a simple batteries included Compojure library and nothing more.

## Minimum Application

```clojure
(ns orchid.sample-app
  (:require [orchid.core :refer [GET grow defroutes not-found]]))

(defroutes app
  (GET "/" [] "hello world!")
  (not-found "not found")

(defn -main []
  (grow app 8080))
```

To immediately start working with an application you can start the server with

`lein run`

We will automatically reload code that changes, so no need to restart the server periodically.
If you prefer working from a REPL or using Cider just execute `grow` from the REPL.

## Routes

Routes are handled just like they are in Compojure. Orchid provides middleware
that handles the automatic conversion of JSON to Clojure data-structures.

### Examples

I've included some examples of common routing. For more information on Compojure routes I recommend
checking out the Compojure documentation on routes: https://github.com/weavejester/compojure/wiki/Routes-In-Detail

As well as the article on destructuring: https://github.com/weavejester/compojure/wiki/Destructuring-Syntax

I included some sample requests using httpie: http://httpie.org

### JSON Response

```clojure
(GET "/json" [] (json-response {:message "hello world!"}))
;; http get localhost:8080
```

### Query Parameters

```clojure
(GET "/hello" [name] (str "hi " name))
;; http get localhost:8080/hello?name=Brian
```

### URL Parameters

```clojure
(GET "/hello/:name" [name] (str "hi " name))
;; http get localhost:8080/hello/Brian
```

### JSON Body

We can access the body like this:
```clojure
(POST "/hello" {body :body} (str "hi " (:name body)))
;; echo '{"name": "Brian"}' | http post localhost:8080/hello --json
```

Or we can use destructuring to pull out specific keys we want.
```clojure
(POST "/hello" {{:keys [name]} :body} (str "hi " name))
;; echo '{"name": "Brian"}' | http post localhost:8080/hello --json
```

## License

Copyright © 2016 Brian Dawn

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
