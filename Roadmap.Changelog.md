# Roadmap/Ideas/Bugs:

* (!) Load project classpath for `lein shegon` inside of a project
* Embedding REPLs into other pages
** With bookmarklet!
* Run shegon somewhere on the internet
* Optimize stupid output code
* Output embetterments: clickable code snippets and stuff
* (!) Documentation
* (!) Figure out how to test the thing
* Allow defmacro in REPL
* Screencast
* Figure out how to make it embeddable into others' web servers
* Don't run incomplete declarations in REPL
* Nicer emit-js, maybe even live emit-js
* Move REPL to a separate package

# Version Log

* master:
    * reload code on `lein run`
    * anti-csrf
    * some tests and Travis integration

* 0.1.1
    * `lein shegon`, `(include-cljs)`, `(run-if-not-running)`
