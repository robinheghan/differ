;; Copyright © 2014-2017 Robin Heggelund Hansen.
;; Distributed under the MIT License (http://opensource.org/licenses/MIT).

(ns differ.core
  "This namespace has two functions: diff and patch.

  Diff allows you to compare two datastructures, and returns elements that
  are different or non-existant. The result of this comparision is represented
  by a vector containing alterations and removals, respectively.

  Once you have a diff like this, you can apply it to any similar data-
  structure with the patch function. If you only want a diff of alterations,
  or only removals, you can use the alteration and removal functions in the
  differ.diff and differ.patch namespaces."
  (:require [differ.diff :as diff]
            [differ.patch :as patch]))

(defn diff
  "Returns a vector containing the differing, and non-existant elements, of
  two clojure datastructures."
  [state new-state]
  [(diff/alterations state new-state)
   (diff/removals state new-state)])

(defn patch
  "Applies a diff, as created by the diff function, to any datastructure."
  [state [alterations removals]]
  (-> state
      (patch/removals removals)
      (patch/alterations alterations)))
