# arraklisp

It's a *very* basic Lisp - with *really* nerdy Dune themes.

## Installation

Clone, `lein uberjar`, usage below.
If you really want a jar for some reason lemme know.

## Usage

    $ java -jar arraklisp-0.1.0-SNAPSHOT-standalone.jar

## Examples

`<terrible purpose>` is the prompt.
Arithmetic wise, it supports `+`, `-`, `*`, `/`.
~~~
<terrible purpose>
(+ 1 2)
=> 3 :: NUMBER
~~~

You can define functions using `kwisatz`.
`arrakis` is like `def` - it puts things into the global scope.
~~~
<terrible purpose>
(arrakis f (kwisatz { x} (+ 1 x)))
def'd
<terrible purpose>
(f 5)
=> 6 :: NUMBER
~~~

You can define let-expressions using `muaddib`.
~~~
(muaddib [ (x 4) (y 3)] (* x y))
=> 12 :: NUMBER
~~~

Functions will display terribly non-useful information.
~~~
<terrible purpose>
(kwisatz { x} (+ x 1))
=> kwisatz... :: FUNCTION
~~

Make a mistake? Have a great dune quote! (There's only one.)
~~~
<terrible purpose>
(arks x 2)
May thy knife chip and shatter!
~~~

~~~
exit
~~~


### Known Issues

I haven't figured out `instaparse` fully yet, so there are some syntax peculiarities.

In function arguments (`kwisatz`), if you specify more than 0, you must preface them with a space.
In let-exprs (`muaddib`), if you specify more than 0 pairings, you must preface them with a space.

## License

https://en.wikipedia.org/wiki/WTFPL
