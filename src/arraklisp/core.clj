(ns arraklisp.core
  (:require [instaparse.core :as insta]
            [clojure.core.match :as cmatch])
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

(defn call-sym
  "lookup a function at given symbol and evaluate a call - error if not a function"
  [sym scope args]
  ;; put built in functions here
  (cmatch/match (lookup sym scope)
                [:+] (cljnum->alnum (apply + (map (comp read-string second) args)))
                [:/] (cljnum->alnum (apply / (map (comp read-string second) args)))
                [:*] (cljnum->alnum (apply * (map (comp read-string second) args)))
                [:-] (cljnum->alnum (apply - (map (comp read-string second) args)))
                [:FUN & params-body] (eval-al (last params-body)
                                              (extend-scope (butlast params-body) args scope))
                :else (throw (Exception. (str "attempting to call a non-function " sym)))))

(defn eval-al
  "evaluates a given arraklisp expression"
  [tree scope]
  (cmatch/match tree
         [:SYM sym] (lookup sym scope)
         [:NUM num] (read-string num)
         [:FUN & params-body] [:FUN (butlast params-body) (last params-body)]
         [:CALL [:SYM sym] & args] (call-sym sym scope args)
         [:CALL [:FUN & params-body] & args] (eval-al (last params-body)
                                                      (extend-scope (butlast params-body)
                                                                    args
                                                                    scope))))
(defn top-eval
  "top level evaluation"
  [in-str]
  (let [init-scope {"+" [:+] "-" [:-] "/" [:/] "*" [:*]}
        evald (eval-al (first (to-syntax in-str)) init-scope)]
    (cmatch/match evald
           [:NUM num] (str num " :: NUMBER")
           [:FUN & stuff] "kwisatz... :: FUNCTION")))

;;(defn gogo-repl
  ;;"go repl!"
  ;;[scope]
  ;;(loop [repl-scope scope]


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
