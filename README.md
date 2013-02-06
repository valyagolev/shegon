# shegon

Shegon is a library for serving ClojureScript assets (during development).

It provides a function `(include-cljs "my.module" "another.one")` which
returns a series of `<script>` tags to use, and a REPL right in your browser,
which is able to reload code from your modules on the fly, without reloading
a page.

## Serving ClojureScript

This is pretty straight-forward:

1. Add `[shegon "0.1.1"]` to your `:dependencies`
2. Use `(shegon.server/include-cljs "my.cljs.module" "another.one")` to get
`<script>` tags, for example when rendering the `<head>` tag.

**Please note** that all the modules should be on your `classpath` (unlike when
using `cljsbuild`), for example in your `src` folder!

It works by running a special server inside your app and giving you links to
the compiled javascripts served using that very server. I hope it's not too
intrusive, you should hardly notice it if I've done it right.

A screenshot:

![script tags](https://ucarecdn.com/21b2bf97-a362-48a4-835c-304b1a80c2a5/)

Please note that CLJS is compiled when you use `(include-cljs)`, not when the
file is served! If you don't want to run your own server to develop JS, try the
next section: REPL.

## REPL

Shegon REPL is a ClojureScript REPL which runs right on the webpage. I'll make
including it into your own webpage easier one day, for now you can just use the
special REPL page:

![here we go](https://ucarecdn.com/472df9f8-a8e4-492b-95b2-7ae7d61e330a/)

![namespaces](https://ucarecdn.com/34167742-0b93-44c7-9215-66f91e6b4549/)

If you want to use it in your project, try the first section (Serving
ClojureScript) about how to install shegon. The very same server which serves
the compiled JS serves the REPL. Open [http://127.0.0.1:19000/](http://127.0.0.1:19000/)
and behold. (If you are still not using `(include-cljs)`, there is a function
`(shegon.server/run-if-not-running)`). Take a look:

![loading modules](https://ucarecdn.com/de8cd031-add1-4d2f-a763-5ec3fde389fc/)

If you don't want to create any projects you can install a `lein` plugin. Add
`[lein-shegon "0.1.1"]` to your `:user :plugins` setting in
`~/.lein/profiles.clj`. If the file is still empty this should work:

    {:user {:plugins [[lein-shegon "0.1.1"]]}}

If it's not you already know what to do.

After that you can just run:

    lein shegon

And open [http://127.0.0.1:19000/](http://127.0.0.1:19000/) to get REPL'd.

If you run it inside of a project it could use its classpath to `require` stuff
but it does not yet :( So use `(run-if-not-running)` inside projects.

## License

Copyright (C) 2013 Valentin Golev (me@valyagolev.net)

Distributed under the Eclipse Public License, the same as Clojure.

