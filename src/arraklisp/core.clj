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
    LETPAIR = (<' ('> SYM <' '> AL <')'>)
    LET = <'(muaddib ['> LETPAIR* <'] '> AL <')'>
    DEF = <'(arrakis '> SYM <' '> AL <')'>"))

(declare eval-al)

(defn cljnum->alnum
  "converts a clojure Number into an Arraklisp number"
  [num]
  [:NUM (str num)])

(defn alnum->cljnum
  "converst al num to clojure number"
  [alnum]
  (m/match alnum
         [:NUM num] (read-string num)
         :else (throw (Exception. (str alnum " is not a number!")))))

(defn prim-apply
  "applies operation to ASTs, if not nums, error out"
  [op asts scope]
  (let [nums (map (fn [x] (alnum->cljnum (eval-al x scope))) asts)]
    (cljnum->alnum (apply op nums))))

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


(defn call-fun
  "call a function using its lexical scope"
  [fun args scope]
  (let [expr (eval-al fun scope)
        e-args (map (fn [x] (eval-al x scope)) args)]
    (m/match expr
             [:EXPR :+ env] (prim-apply + args env)
             [:EXPR :/ env] (prim-apply / args env)
             [:EXPR :* env] (prim-apply * args env)
             [:EXPR :- env] (prim-apply - args env)
             [:EXPR {:params params :body body :env env}] (eval-al body
                                                                   (extend-scope (vec (map second params))
                                                                                 e-args
                                                                                 env))
             :else (throw (Exception. (str "only functions are callable, not " expr))))))

(defn eval-let
  "turns a LET expr into a CALL / FUN expr"
  [labeled-pairings body scope]
  (if (nil? labeled-pairings)
    (eval-al body scope)
    (let [syms (vec (map second labeled-pairings))
          vals (vec (map (fn [x] (nth x 2)) labeled-pairings))
          fun (vec (concat [:FUN] (concat syms [body])))
          call-s (concat [:CALL fun] vals)]
      ;; (println (vec call-s))
      (eval-al (vec call-s) scope))))

(defn eval-al
  "evaluates a given arraklisp expression"
  [tree scope]
  (m/match tree
         [:SYM "+"] [:EXPR :+ scope]
         [:SYM "-"] [:EXPR :- scope]
         [:SYM "/"] [:EXPR :/ scope]
         [:SYM "*"] [:EXPR :* scope]
         [:SYM sym] (eval-al (lookup sym scope) scope)
         [:NUM num] [:NUM num]
         [:FUN & params-body] [:EXPR {:params (butlast params-body)
                                      :body (last params-body)
                                      :env scope}]
         [:EXPR & stuff] (vec (cons :EXPR stuff))
         [:LET & stuff] (eval-let (butlast stuff) (last stuff) scope)
         [:CALL [:SYM sym] & args] (eval-al (vec (concat [:CALL (eval-al [:SYM sym] scope)] args)) scope)
         [:CALL fun & args] (call-fun fun (map (fn [x] (eval-al x scope)) args) scope)))

(defn top-eval
  "top level evaluation"
  [in-str init-scope]
  (let [evald (eval-al (first (to-syntax in-str)) init-scope)]
    (m/match evald
             [:NUM num] (str "=> " num " :: NUMBER")
             [:EXPR & stuff] "=> kwisatz... :: FUNCTION")))

(defn gogo-repl
  "go repl!"
  [scope]
  (println "<terrible purpose>")
  (let [input (read-line)]
    (when-not (= "exit" input)
      (let [before (first (to-syntax input))]
        (try
          (m/match before
                   [:DEF sym al] (do (println "def'd")
                                     (gogo-repl (extend-scope [(second sym)]
                                                              [(eval-al al scope)]
                                                              scope)))
                   _ (do (println (top-eval input scope))
                         (gogo-repl scope)))
          (catch Exception e (do (println "May thy knife chip and shatter!")
                                 (gogo-repl scope))))))))



(defn -main
  "I run the arraklisp REPL."
  [& args]
  (gogo-repl {}))
