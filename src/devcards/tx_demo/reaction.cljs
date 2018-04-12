(ns tx-demo.reaction
  (:require [devcards.core]
            [reagent.ratom :as r])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [tx-demo.macros :refer [eval-block code-block]]))

(def base (r/atom 1))

(defn last-rf
  "Reducing fn that acts like last on a sequential collection"
  ([] nil)
  ([final] final)
  ([_ next] next))

(def tx-1 (comp (map inc) (filter even?)))

(defn reaction-1 [tx input]
  (r/make-reaction
   (fn []
     (transduce tx last-rf [@input]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Proper Way
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend-type r/Reaction
  IReduce
  (-reduce
    ([this f]
     (-reduce this f (f)))
    ([this f start]
     (let [last (volatile! start)]
       (r/make-reaction
        (fn []
          (let [next (f @last @this)]
            (vreset! last next)
            next))
        :auto-run true)))))

(defn reaction [tx input]
  (transduce tx last-rf input))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Examples
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def follow (r/make-reaction (fn [] @base)))

(def sum (reduce + follow))

(def t (reaction tx-1 sum))

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
