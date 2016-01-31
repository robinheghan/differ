# Differ

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

## Maps

Maps are probably the best supported, and most straight forward type to diff. Alterations are a simple map of the key-value pairs missing. Removals are a map of keys where the value is 0, or a nested datastructure. Check the "Usage" section for a decent example.

## Sequential types

Differ works by checking what values have changed for a given key. For sequential types (vectors, lists and seqs) this means that alterations is represented as a sequential type of `[index diff]` for every key that has a changed value. This unfortunetly means that differ does not detect if elements have simply changed places.

Removals are represented as a sequential type containing number of elements to drop from the end of the sequence, and `[index diff]` for every nested type that contains removals (everything else is an alteration).

Differ does diff between sequential types, but remains the correct type of the new state.

```clojure
(ns test
  (:require [differ.diff :as diff]))

(diff/alterations '(1 2 3) [1 2 2 4])
;; [2 2 :+ 4]

(diff/removals [1 {:a 2} 3] '(1 {}))
;; (1 1 {:a 0})
```

## Sets

Because differ works by checking if the value for a given key has changed, sets does not support nesting (every element is it's own key). Differ can therefore only detect if elements have been added or removed from a set, and not if they have changed. If you have sets in your datastructure, you should keep them shallow to avoid a large diff.

```clojure
(ns test
  (:require [differ.diff :as diff]))

(diff/alterations #{1 2 3} #{1 2 3 4})
;; #{4}

(diff/removals #{1 {:a 2} 3} #{{} 1})
;; #{{:a 2} 3} <-- does not pick up changes
```

## Contributing

Feedback to both this library and this guide is welcome. Plese read `CONTRIBUTING.md` for more information.

### Running the tests

Differ is assumed to work with Clojure 1.7 and up, as well as Clojurescript 1.7.228 and up.

There is a leiningen alias that makes it easy to run the tests against supported Clojure versions:

```bash
λ lein all-tests
```

## License

Copyright © 2014-2016 Robin Heggelund Hansen.

Distributed under the [MIT License](http://opensource.org/licenses/MIT).
