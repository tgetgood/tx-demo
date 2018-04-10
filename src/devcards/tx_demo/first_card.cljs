(ns tx-demo.first-card
  (:require-macros
   [devcards.core :refer [defcard-doc
                          defcard-rg
                          mkdn-pprint-source]])
  (:require
   [devcards.core]
   [reagent.core :as reagent]))


(defonce app-state (reagent/atom {:count 0}))

(defn on-click [ratom]
  (swap! ratom update-in [:count] inc))

(defn counter [ratom]
  (let [count (:count @ratom)]
    [:div
     [:p "Current count: " count]
     [:button
      {:on-click #(on-click ratom)}
      "Increment"]]))

(defcard-rg counter
  [counter app-state]
  app-state
  {:inspect-data true})

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

(defcard-rg reduce-basics
  "We all know how reduce works, it's the encapsulation of recursion over a
  collection independent of the actual collection type. So"
  [:div
   [:div (str '(reduce + [1 4 5 3 2]) " => ") (reduce + [1 4 5 3 2])]
   [:br]
   [:div (str "Now imagine we have something like " [:one :four :five :three :two])]
   [:br]
   [:div (str "We can still use reduce, but first we have to transform the input vector: " '(reduce + (map to-number [:one :four :five :three :two])) " => " )]
   ]
  )
