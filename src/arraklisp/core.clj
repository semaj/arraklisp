(ns arraklisp.core
  (:require [instaparse.core :as insta])
  (:gen-class))

(def to-syntax
  (insta/parser
   "AL = NUM+ | '%' SYM+ | FUN | CALL
    NUM = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
    SYM = 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z'
    FUN = '(' SYM+ ' [' SYM+ '] ' AL ')'
    CALL = '(' SYM+ ' ' (AL ' ')+ ')'"))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
