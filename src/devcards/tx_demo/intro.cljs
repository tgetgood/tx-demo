(ns tx-demo.intro
  (:require [devcards.core])
  (:require-macros
   [devcards.core :refer [defcard-doc]]
   [tx-demo.macros :refer [eval-block code-block]]))



(defcard-doc
  "## Reduction"
  "Reduce is an abstraction of recursion. The operation of reduce encapsulates
  the recursion so that we can think about what we're doing and forget about how
  we're doing it.

  Under the hood reduce just become recursion, which compiler willing becomes a
  loop. All of the standard sequence functions can be written in terms of reduce: "

  (code-block (filter predicate coll))

  "Is the same as"

  (code-block
   (reduce (fn [acc n] (if (predicate n) (conj acc n) acc)) [] coll))

  "We all know how reduce works, it's the encapsulation of recursion over a
  collection independent of the actual collection type. So "
  (eval-block (reduce + [1 2 3 4 5]))

  "\n\n## Transduction"

  "Imagine now that we do something like:"

  (code-block (reduce + [:one :two :three :four :five]))

  "Of course `+` doesn't know how to deal with these keywords, so we have to do
  instead:"

  (eval-block (reduce + (map to-number [:one :two :three :four :five])))

  "What we're effectively saying is \"as you reduce, perform this operation
  `to-number` along the way\".

   That's really all transduction is:"

  (eval-block (transduce (map to-number) + [:one :two :three :four :five]))

  "We can chain transducers the same way we would chain sequence operators:"

  (eval-block
   (transduce (map to-number) + [:one :two :three :four :five])
   (transduce (comp (map to-number) (filter even?))
              + [:one :two :three :four :five])
   (transduce (comp (map to-number) (take-while odd?))
              + [:one :two :three :four :five]))

  "Remember that reduce abstracts away the idea of recursion over a sequence, so
  the fact that transducers separate the transformation from the reduction means
  that they separate the idea of transformation from the type of the data being
  transformed.

  This is the important takeaway: transduction enables new kinds of code reuse
  by separating what you want to do to items from how they are stored and
  accessed.

  To put that in context, traditionally every time you create a new container
  type (something collection like) one of the first things you'll need is to
  implement map for it. Sequences, maps, channels, (plus every monad) has a map
  (lift) operation defined on it.

  Transducers allow us to separate these concerns. The transducer defines how to
  perform an operation, and the container is responsible for defining how to
  recurse itself. In clojure this is accomplished via the IReduce protocol
  (mostly, core.async actually handles transducers on channels quite differently)."
  )
