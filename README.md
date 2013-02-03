# shegon

Just a take on making REPL for ClojureScript. Re-inventing the wheel or not.

The best thing? It can `require` modules, reloading them if necessary, so
it may be actually a little bit close to being useful.

Also, a simple API/web service for compiling chunks of ClojureScript code,
so you can `eval` and everything.

## Usage

You should run:

    lein cljsbuild auto

And in another terminal:

    lein run

Then open your broswer: http://localhost:8080/ and behold:

.

Create some `.cljs` file in the classpath, as you would do with `.clj` file.
Try to `(require 'my.module)` it (only this simplest syntax form works).


## License

Copyright (C) 2011 FIXME

Distributed under the Eclipse Public License, the same as Clojure.

