(ns tx-demo.transducers
  (:require [devcards.core])
  (:require-macros
   [devcards.core :refer [defcard-doc]]
   [tx-demo.macros :refer [eval-block code-block]]))

(defcard-doc
  "# The Essence of Mapping"

  "Here's the implementation of the transducer part of `cljs.core/map`:"

  (code-block
   "(defn map [f]
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (rf result (f input)))
      ([result input & inputs]
       (rf result (apply f input inputs))))))")

  "And `interpose`:"

  "```clj
(defn interpose [sep]
  (fn [rf]
    (let [started (volatile! false)]
      (fn
        ([] (rf))
        ([result] (rf result))
        ([result input]
          (if @started
            (let [sepr (rf result sep)]
              (if (reduced? sepr)
                sepr
                (rf sepr input)))
            (do
              (vreset! started true)
              (rf result input)))))))
```"

  "Scary, right? And the kicker is that 90% of that code is just boiler
  plate. Confusing, scary boilerplate. So let's get rid of it:"

  (code-block
   "(defn map [f]
  (make-transducer
   {:next-fn (fn [next] {:emit (f next)})}))"

   "(defn filter [p]
  (make-transducer
   {:next-fn (fn [next]
               (when (p next)
                 {:emit next}))}))"

   "(defn interpose [sep]
  (make-transducer
   {:init-state {:started? false}
    :next-fn (fn [{:keys [started?]} next]
               (if started?
                 {:emit-multiple [sep next]}
                 {:emit next
                  :reset-state {:started? true}}))}))"

   "(defn partition [part-size]
  (make-transducer
   {:init-state {:buffer []}
    :flush (fn [{:keys [buffer]}]
             {:emit buffer})
    :next-fn (fn [{:keys [buffer]} next]
               (let [next-buffer (conj buffer next)]
                 (if (= part-size (count next-buffer))
                   {:reset-state {:buffer []}
                    :emit next-buffer}
                   {:reset-state {:buffer next-buffer}})))}))")

  "That's it. Map says that every time you get a new input, apply f to
  it. That's as simple as it gets."

  "Filter says, every time you see an element, if it satisfies the predicate,
  pass it into the output, otherwise drop it (pass nothing on to the output)."

  "Interpose is a little more complicated, it says that if you haven't started
  yet, then just pass the first thing you get back out and start. Once you've
  started, send out a separator before each thing you get. That's the essence of
  interposition."

  "Partition is the most complicated yet. It needs to maintain a buffer of
  things seen so far, and every time it gets to the right size, send it to the
  output as a chunk, resetting the buffer for the next element."

  "One new consideration here is: what do we do when we run out of input? Do we
  simply drop all of the buffered entries? Do we pad with nils? Do we return
  what we have even though the chunk is of the wrong length? This is business
  logic and you have to decide yourself, so transducers need to have a way to
  ask you if you want to flush. I've called it flush."

  "Caveat: I haven't actually implemented `make-transducer`, but I'm pretty
  certain I could. It might get a little uglier in real life."

  "## Anatomy of a Transducer"

  (code-block
   "(defn my-transducer [rf]
  (let [state (volatile! {})]                          ; (1) State
    (fn
      ([] (rf))                                        ; (2) Monoid for reduce
      ([final] (rf (rf final (wrap-up state))))        ; (3) Cleanup
      ([so-far next]
        (let [intermediate (rf so-far something)]
          (if (reduced? intermediate)                  ; (4) Early termination
            @intermediate
            (rf intermediate next)                     ; (5) Heart of the Matter
))))))")

  "Transducers aren't allowed to modify the `so-far` or `final` values, and the
  0-arity version has to call the reducing funtion, so exposing any of that
  plumbing to the user is confusing, and worse brittle, since a lot of
  conventions have to be obeyed or your transducer won't play nice with
  others."

  "I'm also not fond of the excessive amounts of mutable state that tend to
  happen inside transducers. They make things like event sourcing and replay
  that we expect to be trivial in cljs apps very difficult, and require thread
  isolation in clj."

  "The last complication is `reduced?`. whenever you call the passed in reducing
  function more than once, you have to check that it didn't interrupt the
  computation."

  "All told, the only things a transducer can do are modify internal state, and
  call the reducing function zero or more times in one of two contexts, so we
  can capture all possible valid uses of transducers and hide most of the
  complexity (even the reducing fn itself) from the user."

  "It's quite possible this scheme won't make all possible transducers, but it
  catches a lot of interesting ones."

  "Questions:
* Why not use a protocol?
* How much slower will this state system be than direct manipulation?")
