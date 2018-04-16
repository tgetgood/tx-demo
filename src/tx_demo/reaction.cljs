(ns tx-demo.reaction
  (:require [devcards.core]
            [reagent.ratom :as r])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [tx-demo.macros :refer [eval-block code-block]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Reactions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def start (r/atom 1))

(def follow (r/make-reaction (fn [] @base)))

(def odd-start?
  (r/make-reaction
   (fn []
     (even? (inc @base) ))))

(let [sum (atom 0)]
  (def sum-1
    (r/make-reaction
     (fn []
       (swap! sum + @base)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Reduction for Reactions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend-protocol IReduce
  r/Reaction
  (-reduce
    ([this f]
     (-reduce this f (f)))
    ([this f start]
     (let [last (atom start)]
       (r/make-reaction
        (fn []
          (let [next (f @last @this)]
            (reset! last next)
            next))
        :auto-run true))))
  r/RAtom
  (-reduce
    ([this f]
     (-reduce this f (f)))
    ([this f start]
     (let [last (atom start)]
       (r/make-reaction
        (fn []
          (let [next (f @last @this)]
            (reset! last next)
            next))
        :auto-run true)))))

(defn last-rf
  "Reducing fn that acts like last on a sequential collection"
  ([] nil)
  ([final] final)
  ([_ next] next))

(defn reaction
  ([input] input)
  ([tx input]
   (transduce tx last-rf input)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Examples
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def base (r/atom 1))

(def follow-1 (reaction base))

(def odd-base?
  (reaction (comp (map inc) (map even?)) base))

(def sum (reduce + base))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Cards
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defcard-doc
  "So the basic idea of a transducer is to separate transformation from
  recursion, so we should be able to extend any data type so that transduction
  works on it. Let's try Reagent's reactions.

  We have the following constructors to create data structures from a transducer
  and a base data structure.\n\n"

  (code-block
   (into [] tx coll)
   (sequence tx coll)
   (chan tx c))

  "And we want to add another along the lines of"

  (code-block (reaction tx r))

  "This could be done in an ad-hoc fashion by something like"

  "```clj
(defn last-rf
  \"Reducing fn that acts like last on a sequential collection\"
  ([] nil)
  ([final] final)
  ([_ next] next))

(defn reaction-1 [tx input]
  (r/make-reaction
   (fn []
     (transduce tx last-rf [@input]))))
```")

(defn xform [coll]
  (->> coll
       (map inc)
       (filter even?)))

(def txform (comp (map inc) (filter even?)))
