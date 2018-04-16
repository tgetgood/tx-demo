(ns tx-demo.map
  (:require [devcards.core]
            [reagent.ratom :as r])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [tx-demo.macros :refer [eval-block code-block]]))

(defcard-doc
  "# A (Very Brief) History of Map"

  "The modern idea of mapping was introduced to computer languages as `mapcar`
  in the very first versions of LISP in the late 50s."

  "It has since been reinvented dozens of times and reimplemented hundreds if
  not thousands of times."

  "You can implement the concept of mapping for any programming construct that
  acts like a container, that is holds data (including all datastructures). And
  people have."

  "Unsurprisingly it turns out that there's a much deeper mathematics at
  work. There's something fudamental about mapping (and it's not about
  monads)."

  "We're going to explore that a bit."

  "# Map as Implemented in Clojure"

  "In Clojure, `map` can be applied to any of the built in collections. This
  isn't accomplished by separate implementations, but via polymorphism."

  "`map` is implemented in terms of the `ISeq` protocol, so that any collection
  that impelements it can be passed into `map`."

  "This saves us from having N data structures with N functions. The problem is
  that we lose types. No matter what the input type, `map` always returns a
  `LazySeq`. If you want a different collection type, there's always `into`, but
  that comes at the cost of making a two collections instead of one."

  ""

  "Let's look at how a simplified map works conceptually:"

  (code-block
   "(defn map [f coll]
  (when coll
    (cons (f (first coll)) (map f (next coll)))))")

  "Now we can see that map is really doing three things:"

  "1. Breaking down the input collection with `first` & `next`
2. Transforming the pieces with `f`
3. Building up the result with `cons`"

  "Clojure's `map` is generic in the first 2 aspects, but not the third."

  "More importantly `ISeq` only applies to sequences in memory, not to sequences
  in time. So Clojure still needs totally separate implementations of `map` for
  core.async channels, streams, observables, etc.."

  "Let's see what we can do about that."
  )
