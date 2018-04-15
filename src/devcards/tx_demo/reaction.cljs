(ns tx-demo.reaction
  (:require [devcards.core]
            [reagent.ratom :as r])
  (:require-macros [devcards.core :refer [defcard-doc]]
                   [tx-demo.macros :refer [eval-block code-block]]))

(defn last-rf
  "Reducing fn that acts like last on a sequential collection"
  ([] nil)
  ([final] final)
  ([_ next] next))

(def tx-1 (comp (map inc) (filter even?)))

(defn reaction-1 [xform input]
  (r/make-reaction
   (fn []
     (transduce xform last-rf [@input]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Proper Way
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

(defn reaction [tx input]
  (transduce tx last-rf input))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;; Examples
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def base (r/atom 1))

(def even+1?
  (r/make-reaction
   (fn []
     (even? (inc @base) ))))

#_(re-frame/reg-sub :t
        [:base]
        (fn [base]
          (even? (inc base))))

(def t (reaction (comp (map inc) (map even?)) base))

(let [sum (atom 0)]
  (def sum-1
    (r/make-reaction
     (fn []
       (swap! sum + @base)))))

(def temp (atom 0))

#_(re-frame/reg-sub
 :sum
 [:base]
 (fn [acc base]
   (swap! temp + base)))

#_(re-frame/reg-event-db
 :click
 (fn [db [_ val]]
   (update db + val)))

(def follow (r/make-reaction (fn [] @base)))

(def sum (reduce + base))


(map (comp even? inc)) (comp (map inc) (map even?))

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
