# Changelog

## 0.4.0

- Update dependencies and build targets
- Add generative tests
- Add deps.edn support (via git coordinate)
- Fix issue where falsey map keys were not handled correctly by diff or patch.
- Fix issue where collections could not be cleared.
- Fix issue #3 where patch throws instead of replacing a vector with an number.
- Fix issue #2 where records were treated as maps and threw

## 0.3.3

- Update dependencies and build targets
- Fix runtime crash where old value was vector and new value was nil

## 0.3.2

Change in metadata

## 0.3.1

Only a change in meta data (project moved from github to gitlab).

## 0.3

This release bumps the required version of Clojure and Clojurescript to 1.7.x.

* Issue 20: Patch doesn't retain metadata
* Issue 19: Vector patch fails with null pointer exception
* Issue 16: Switch from cljx to cljc

## 0.2.2

* Issue 15: Edge case with `nil`

## 0.2.1

* Issue 13 and 14: Edge cases with `nil`

## 0.2

First release with support for all built-in data structures
