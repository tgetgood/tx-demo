(ns tx-demo.transducers
  (:require [devcards.core])
  (:require-macros
   [devcards.core :refer [defcard-doc]]
   [tx-demo.macros :refer [eval-block code-block]]))

(defcard-doc
  "# That Essence..."

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

   "(defn interpose [sep]
  (make-transducer
   {:init-state {:started? false}
    :next-fn (fn [{:keys [started?]} next]
               (if started?
                 {:emit-multiple [sep next]}
                 {:emit next
                  :reset-state {:started? true}}))}))")

  "That's it. Map says that every time you get a new input, apply f to
  it. That's as simple as it gets."

  "Interpose is a little more complicated, it says that if you haven't started
  yet, then just pass the first thing you get back out and start. Once you've
  started, send out a separator before each thing you get. That's the essence of
  interposition."

  "Caveat: I haven't actually implemented `make-transducer`, but I'm pretty
  certain I could. It might get a little uglier in real life."

  "## Anatomy of a Transducer"

  (code-block
   "(defn my-transducer [rf]
  (let [state (volatile! {})]                          ; (1) State
    (fn
      ([] (rf))                                        ; (2) Monoid for reduce
      ([final] (rf (wrap-up final)))                   ; (3) Cleanup
      ([so-far next]
        (let [intermediate (rf so-far something)]
          (if (reduced? intermediate)                  ; (4) Early termination
            @intermediate
            (rf intermediate next)                     ; (5) Heart of the Matter
))))))"))
