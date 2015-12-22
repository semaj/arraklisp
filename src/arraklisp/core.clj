(ns arraklisp.core
  (:require [instaparse.core :as insta]
            [clojure.core.match :as m])
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

(defn cljnum->alnum
  "converts a clojure Number into an Arraklisp number"
  [num]
  [:NUM (str num)])

(defn extend-scope
  "add syms-values zipped map to the given scope"
  [syms values scope]
  (reduce (fn [m [sym value]] (merge m {sym value}))
          scope
          (map vector syms values)))

(defn lookup
  "lookup and retrieve the given sym in the scope map"
  [sym scope]
  (or (scope sym)
      (throw (Exception. (str "symbol " sym " not found")))))

(declare eval-al)

(defn call-fun
  "call a function using its lexical scope"
  [fun args scope]
  (let [expr (eval-al fun scope)]
    (m/match expr
             [:EXPR :+] (cljnum->alnum (apply + args))
             [:EXPR :/] (cljnum->alnum (apply / args))
             [:EXPR :*] (cljnum->alnum (apply * args))
             [:EXPR :-] (cljnum->alnum (apply - args))
             [:EXPR {:params params :body body :env env}] (eval-al body
                                                                   (extend-scope params
                                                                                 args
                                                                                 env))
             :else (throw (Exception. (str "only functions are callable, not " expr))))))

(defn eval-al
  "evaluates a given arraklisp expression"
  [tree scope]
  (m/match tree
         [:SYM sym] (lookup sym scope)
         [:NUM num] (read-string num)
         [:FUN & params-body] [:EXPR {:params (butlast params-body)
                                      :body (last params-body)
                                      :env scope}]
         [:EXPR & stuff] (vec (cons :EXPR stuff))
         [:CALL [:SYM sym] & args] (eval-al (vec (concat [:CALL (lookup sym scope)] args)) scope)
         [:CALL fun & args] (call-fun fun (map (fn [x] (eval-al x scope)) args) scope)))

(defn top-eval
  "top level evaluation"
  [in-str]
  (let [init-scope {"+" [:EXPR :+] "-" [:EXPR :-] "/" [:EXPR :/] "*" [:EXPR :*]}
        evald (eval-al (first (to-syntax in-str)) init-scope)]
    (m/match evald
             [:NUM num] (str num " :: NUMBER")
             [:EXPR & stuff] "kwisatz... :: FUNCTION")))

;;(defn gogo-repl
  ;;"go repl!"
  ;;[scope]
  ;;(loop [repl-scope scope]


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
