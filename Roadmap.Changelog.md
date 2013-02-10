# Roadmap/Ideas/Bugs:

* (!) Load project classpath for `lein shegon` inside of a project
* Figure out how to make it embeddable into others' web servers
* Output embetterments: clickable code snippets and stuff
* (!) Documentation
* Screencast

* Repl:
** Run REPL somewhere on the internet
** Move REPL to a separate package
** Allow defmacro in REPL
** Nicer emit-js, maybe even live emit-js
** Don't run incomplete declarations in REPL
** Embedding REPLs into other pages
*** With bookmarklet!

* Tests:
** Move testing library to a separate package, I like it already
** Show lines of code or something, because `TypeError: Cannot call method 'call' of undefined`
   drives me crazy
** Make async-test much-much better

* Blog:
** PhantomJS Jasmine ClojureScript testing on Travis CI (that's a lot of buzzwords!)

# Version Log

* master:
    * reload code on `lein run`
    * anti-csrf
    * some tests and Travis integration

* 0.1.1
    * `lein shegon`, `(include-cljs)`, `(run-if-not-running)`
