(ns tx-demo.complexities
  (:require-macros
     [devcards.core :refer [defcard-doc]]
     [tx-demo.macros :refer [eval-block code-block]]))

(defcard-doc
  "## Anatomy of a Transducer\n\n"

  "The definition of a transducer looks like this:

```clj
(defn my-transducer [rf]
  (let [state (volatile! {})]                          ; (0) State
    (fn
      ([] (rf))                                        ; (1) Monoid for reduce
      ([final] (rf (wrap-up final)))                   ; (2) Cleanup / normal termination
      ([so-far next]
        (let [intermediate (rf so-far something)]
          (if (reduced? intermediate)                  ; (3) Early termination
            @intermediate
            (rf (somehow-combine so-far intermediate)) ; (4) Heart of the Matter
))))))
```"

  "\n## Examples of Transducers\n\n"

  "For example, here's the transducer for `map`:\n\n"

  "
```clj
(fn [rf]
  (fn
    ([] (rf))
    ([result] (rf result))
    ([result input]
       (rf result (f input)))
    ([result input & inputs]
       (rf result (apply f input inputs)))))
```\n\n"

  "And here's the more complex transducer for `interpose`:\n\n"

  "
```clj
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
```")
