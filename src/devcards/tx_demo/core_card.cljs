(ns tx-demo.core-card
  (:require-macros
   [devcards.core :as dc])
  (:require
   [tx-demo.intro]
   [tx-demo.complexities]))

(defn reload []
 (dc/start-devcard-ui!) )
