(ns tx-demo.macros
  (:require [clojure.pprint :refer [pprint]]))

(defmacro repl [form]
  `(str ~(str form) "\n  => " ~form))

(defmacro eval-block
  {:style/indent 0}
  [& forms]
  `(str "\n```clj \n"
        ~@(interpose "\n\n" (map (fn [x#] `(repl ~x#)) forms))
       "\n```\n"))

(defmacro code-block
  {:style/indent 0}
  [& forms]
  `(str "\n```clj \n"
        ~@(interpose "\n\n" (map str forms))
       "\n```\n"))
