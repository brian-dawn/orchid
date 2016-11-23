# orchid

A Clojure library to provide sane REST defaults for a Compojure project.

[![Clojars Project](http://clojars.org/orchid/latest-version.svg)](https://clojars.org/orchid)

[![Build Status](https://travis-ci.org/brian-dawn/orchid.svg?branch=master)](https://travis-ci.org/brian-dawn/orchid)
[![Dependencies Status](https://jarkeeper.com/brian-dawn/orchid/status.svg)](https://jarkeeper.com/brian-dawn/orchid)

## Rationale

I often want to write quick JSON rest APIs. Doing this with plain Compojure can be a bit of a headache at first,
there are a couple dependencies that need to be added to get everything wired together (at least the way I like it)
and I often found myself writing very similar code multiple times.

Orchid aims to just be a simple batteries included Compojure library and nothing more. Orchid may not work for everyone,
but it is very easy to go to a traditional Compojure application from an Orchid one.

## Minimum Application

To get started from a blank slate:

`lein new app myproject`

Then just add orchid as a dependency in your project.clj file. Place the following inside your `core.clj` file.

```clojure
(ns myproject.core
  (:require [orchid.core :refer [GET grow defroutes not-found]]))

(defroutes app
  (GET "/" [] "hello world!")
  (not-found "not found")

(defn -main []
  (grow app 8080))
```

To immediately start working with an application you can start the server with:

`lein run`

Orchid will automatically reload code changes, so you won't need to run `lein run` very often.
If you prefer working from a REPL or using Cider just execute `grow` from the REPL.

## Routes

Routes are handled just like they are in Compojure. If you want information about them I recommend the following
links:

https://github.com/weavejester/compojure/wiki/Routes-In-Detail
https://github.com/weavejester/compojure/wiki/Destructuring-Syntax

### Examples

I included some sample requests using httpie: http://httpie.org

### JSON Response

Any collection returned by a handler will get coerced to JSON.

```clojure
(GET "/json" [] {:message "hello world!"})
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
