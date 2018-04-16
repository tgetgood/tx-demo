(ns tx-demo.reduction
  (:require [devcards.core])
  (:require-macros
   [devcards.core :refer [defcard-doc]]
   [tx-demo.macros :refer [eval-block code-block]]))

(def to-number
  {:one   1
   :two   2
   :three 3
   :four  4
   :five  5
   :six   6
   :seven 7
   :eight 8
   :nine  9
   :zero  0})

(defcard-doc
  "# Reduction"

  "Let's rewrite `map` in terms of `reduce`:"

  (code-block
   "(defn map [f coll]
  (reduce (fn [so-far next]
            (conj so-far (f next)))
          [] coll))")

  "This version of map returns a vector, not a lazy-seq, but is otherwise
  equivalent."

  "How does this compare to our recursive version above? In this version, part
  (1), the breaking down of the collection is entirely handled by `reduce`, but
  we're still on the hook for parts (2) & (3)."

  "In order to separate parts (2) and (3) we need to step back a bit. Consider
  reduce by itself:"

  (eval-block
   (reduce conj [] '(1 2 3 4 5))
   (reduce conj [] #{1 2 3 5 4})
   (reduce conj '() [1 2 3 4 5])
   (reduce + 0 [1 2 3 4 5]))

  "We can see from this that if we ignore the transformation, reduce completely
  separates the decomposition of its input from the construction of the
  result. That's two thirds of the battle."

  "Let's try adding transformation back in:"

  (eval-block
   (reduce + 0 (map to-number [:one :two :three :four :five]))
   (reduce conj #{} [:one :two :three :four :five])
   (reduce conj #{} (map to-number [:one :two :three :four :five])))

  "Of course we're cheating a little by defining map in terms of map, but we're
  not really cheating because we're defining the complected `map` fn of clojure
  in terms of the essence of mapping."

  "The idea of mapping is purely about transformation. That is part (2) of our
  trichotomy. So we can define break down input -> transform -> build up output
  in terms of just the transform."

  "# Transduction"

  "Let's assume for a minute that somebody has isolated the essence of mapping
  and we can get at it by calling map with a single argument `(map
  to-number)`. In fact someone has, and that someone is Rich Hickey. We'll see
  later how this is accomplished, but for now let's look at how it works."

  (eval-block
   (reduce + 0 (map to-number [:one :two :three :four :five]))
   (transduce (map to-number) + 0 [:one :two :three :four :five])

   (reduce conj #{} (map to-number [:one :two :three :four :five]))
   (transduce (map to-number) conj #{} [:one :two :three :four :five]))

  "`transduce` is the function that reassembles our pieces of functionality into
  a result. Inside transduce:

1. The input collection is broken down via `reduce` which `transduce` calls
2. The pieces are transformed by `(map f)`
3. The output is assembled via `conj` with the inital value of `#{}` (the empty
set)"

  "The isolated essence of mapping `(map to-number)` is called a
  transducer. Once we have a transducer representing a transformation, all we
  need to apply that transformation to a new type of thing is implement (1) and
  (3), that is we need to define how to walk the new thing, and how to build a
  new one."

  "That was our goal: define map once and for all. Now that we have `(map f)`,
  we're done."

  "How does `(map f)` work? We'll get to that. But first I'm going to prove that
  we're done by implementing mapping for Reagent's reactions and ratoms without
  implementing `map` at all.")
