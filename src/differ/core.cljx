;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.core
  (:require [differ.diff :as diff]
            [differ.patch :as patch]))

(defn diff
  "Returns the result of diff/alterations and diff/removals in a vector"
  [state new-state]
  [(diff/alterations state new-state)
   (diff/removals state new-state)])

(defn patch
  "Returns the result of applying a patch made by diff to state"
  [state [alterations removals]]
  state)
