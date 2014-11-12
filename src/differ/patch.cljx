;; Copyright © 2014 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.patch)

(defn alterations
  "Returns the result of applying alterations to state"
  [state diff]
  state)

(defn removals
  "Returns the result of applying removals to state"
  [state diff]
  diff)
