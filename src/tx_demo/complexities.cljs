(ns tx-demo.complexities
  (:require [devcards.core])
  (:require-macros
     [devcards.core :refer [defcard-doc]]
     [tx-demo.macros :refer [eval-block code-block]]))

(defcard-doc
  "## Anatomy of a Transducer"

  "The definition of a transducer looks like this:

```clj
(defn my-transducer [rf]
  (let [state (volatile! {})]                          ; (1) State
    (fn
      ([] (rf))                                        ; (2) Monoid for reduce
      ([final] (rf (wrap-up final)))                   ; (3) Cleanup
      ([so-far next]
        (let [intermediate (rf so-far something)]
          (if (reduced? intermediate)                  ; (4) Early termination
            @intermediate
            (rf intermediate next)                     ; (5) Heart of the Matter
))))))
```\n\n"

  "Of the these, (2) & (4) are abstraction leaks from sequences into
  transducers, (1) & (3) are about retaining state externally from the values
  being transformed (1 stores and 3 flushes at the end of the process if
  necessary/applicable), and 5 is really what transduction is about."

  ""

  "## Examples of Transducers"

  "For example, here's the transducer for `map`:"

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
