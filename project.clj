(defproject arraklisp "0.1.0-SNAPSHOT"
  :description "A lisp, featuring Dune references."
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [instaparse "1.4.1"]
                 [org.clojure/core.match "0.3.0-alpha4"]]
  :main ^:skip-aot arraklisp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
