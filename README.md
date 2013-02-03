# shegon

Just my take on making a REPL for ClojureScript. Re-inventing the wheel, probably.

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

![here we go](https://ucarecdn.com/ea3487f3-656f-4bfe-8c2e-d824c44dccfe/)

Create some `.cljs` file in the classpath, as you would do with `.clj` file.
Try to `(require 'my.module)` it (only this simplest form works for now).

## License

Copyright (C) 2013 Valentin Golev (me@valyagolev.net)

Distributed under the Eclipse Public License, the same as Clojure.

