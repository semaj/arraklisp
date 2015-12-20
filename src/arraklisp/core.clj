(ns arraklisp.core
  (:require [instaparse.core :as insta]
            [clojure.core.match :refer [match]])
  (:gen-class))

(def to-syntax
  (insta/parser
   "<AL> = NUM | SYM | FUN | CALL | LET | DEF
    NUM = ('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9')+
    SYM = ('a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '+' | '-' | '\\\\' | '*')+
    FUN = <'(kwisatz {'> (<' '> SYM)* <'} '> AL <')'>
    CALL = <'('> (SYM | FUN) (<' '> AL)* <')'>
    LET = <'(muaddib ['> (<'('> SYM <' '> AL <')'>)* <'] '> AL <')'>
    DEF = <'(arrakis '> SYM <' '> AL <')'>"))

(defn extend-scope
  ""
  [syms values scope]
  (reduce (fn [m [sym value]] (merge m {sym value}))
          scope
          (map vector syms values)))

(defn lookup
  ""
  [sym scope]
  (or (scope sym)
      (throw (Exception. (str "symbol " sym " not found")))))

(defn call-sym
  ""
  [sym scope & args]
  (match (lookup sym scope)
         [:FUN & params body] (eval body (extend-scope params args))
         [_] (throw (Exception. (str "attempting to call a non-function " sym)))))

(defn eval
  "not sure yet"
  [tree scope]
  (match tree
         [:SYM sym] (lookup sym scope)
         [:NUM num] (read-string num)
         [:FUN & params body] [:FUN & body]
         [:CALL [:SYM sym] & args] (call-sym sym scope args)
         [:CALL [:FUN & params body] & args]))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
