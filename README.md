# shegon

![here we go](https://ucarecdn.com/472df9f8-a8e4-492b-95b2-7ae7d61e330a/)

Just my take on making a REPL for ClojureScript. Re-inventing the wheel, probably.

The best thing? It can `require` modules, reloading them if necessary, so
it may be actually a little bit close to being useful. It does not require
`cljsbuild` to be working.

Also, a simple API/web service for compiling chunks of ClojureScript code,
so you can `eval` and everything.

It works with namespaces like a boss (not sure about the sanity of the boss
in question btw):

![namespaces](https://ucarecdn.com/34167742-0b93-44c7-9215-66f91e6b4549/)

## Usage

Run:

    lein ring server

Then open your broswer: http://localhost:3000/ and behold.

## License

Copyright (C) 2013 Valentin Golev (me@valyagolev.net)

Distributed under the Eclipse Public License, the same as Clojure.

