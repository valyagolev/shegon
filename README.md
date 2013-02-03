# shegon

![here we go](https://ucarecdn.com/472df9f8-a8e4-492b-95b2-7ae7d61e330a/)

Just my take on making a REPL for ClojureScript. Re-inventing the wheel, probably.

The best thing? It can `require` modules, reloading them if necessary, so
it may be actually a little bit close to being useful.

Also, a simple API/web service for compiling chunks of ClojureScript code,
so you can `eval` and everything.

It works with namespaces like a boss (not sure about the sanity of the boss
in question btw):

![namespaces](https://ucarecdn.com/36d93c7f-d7df-4306-b16b-81af82d0fc82/)

## Usage

You should run:

    lein cljsbuild auto

And in another terminal:

    lein run

Then open your broswer: http://localhost:8080/ and behold.

## License

Copyright (C) 2013 Valentin Golev (me@valyagolev.net)

Distributed under the Eclipse Public License, the same as Clojure.

