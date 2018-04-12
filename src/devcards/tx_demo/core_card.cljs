(ns tx-demo.core-card
  (:require-macros [devcards.core :as dc])
  (:require [devcards.core]
            [tx-demo.intro]
            [tx-demo.reaction]
            [tx-demo.complexities]))

(defn reload []
 (dc/start-devcard-ui!))
