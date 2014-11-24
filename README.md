# Differ [![Travis CI status](https://api.travis-ci.org/Skinney/differ.png)](http://travis-ci.org/#!/Skinney/differ/builds)

A library for diffing, and patching, Clojure(script) datastructures.

## Motivation

I wanted to implement an auto-save feature for my Clojurescript web-app. To be efficient, only the actual changes should be sent to the backend. `clojure.data/diff` is not the easiest function to work with for several reasons, and there didn't seem to be any good alternatives, so differ was born.

## Setup

Add the following the to your `project.clj`:

[![Clojars Project](http://clojars.org/differ/latest-version.svg)](http://clojars.org/differ)

## Usage

First of all, you need to require the proper namespace:

```clojure
(ns some.ns
  (:require [differ.core :as differ]))
```

You can create a diff using the `differ.core/diff` function:

```clojure
(def person-map {:name "Robin"
                 :age 25
                 :sex :male
                 :phone {:home 99999999
                         :work 12121212})

(def person-diff (differ/diff person-map {:name "Robin Heggelund Hansen"
                                          :age 26
                                          :phone {:home 99999999})

;; person-diff will now be [{:name "Robin Heggelund Hansen"
;;                           :age 26}
;;                          {:sex 0
;;                           :phone {:work 0}]
```

`differ.core/diff` will return a datastructure of the same type that is given, and will work with nested datastructures. If you only want alterations, or removals, instead of both, please check the `differ.diff` and `differ.patch` namespaces.

To apply the diff, you can use the `differ.core/patch` function. This function works on any similar datastructure:

```clojure
(differ/patch {:species :human
               :sex :female}
              person-diff)

;; Will return {:name "Robin Heggelund Hansen"
;;              :age 26
;;              :species :human}
```

## Contributing

Feedback to both this library and this guide is welcome. Plese read `CONTRIBUTING.md` for more information.

### Running the tests

Differ is assumed to work with Clojure 1.6 and up, as well as Clojurescript 2371 and up.

There is a leiningen alias that makes it easy to run the tests against supported Clojure versions:

```bash
λ lein all-tests
```

## License

Copyright © 2014 Robin Heggelund Hansen.

Distributed under the [MIT License](http://opensource.org/licenses/MIT).
