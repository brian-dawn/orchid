# orchid

A Clojure library to provide sane REST defaults for a compojure project.

## Rationale

I felt that using setting up a Compojure application can be difficult for people
coming to Clojure from other ecosystems. Tracking down all the middleware to
enable automatic JSON conversion, setting up auto-reloading/server start from the REPL,
getting prone to aid with debugging will quickly test peoples patience.

Orchid aims to just be a simple batteries included Compojure library and nothing more.

## Routes

Routes are handled just like they are in Compojure. Orchid provides middleware
that handles the automatic conversion of JSON to Clojure data-structures.

### Examples

### Query Parameters

```clojure
(GET "/hello" [name] (str "hi " name))
```

### URL Parameters

```clojure
(GET "/hello/:name" [name] (str "hi " name))
```

### JSON Body

We can access the body like this:
```clojure
(POST "/hello" {body :body} (str "hi " (:name body)))
```

Or we can use destructuring to pull out specific keys we want.
```clojure
(POST "/hello" {{:keys [name]} :body} (str "hi " name))
```

## License

Copyright © 2016 Brian Dawn

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
