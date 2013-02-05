$(function() {

    var loadScript = function(src, cb){
        var script = document.createElement("script");
        script.src = src;
        script.onload = script.onreadystatechange = function(){
            script.onreadystatechange = script.onload = null;

            if (cb)
                cb();
        }
        var head = document.getElementsByTagName("head")[0];
        (head || document.body).appendChild( script );
    }

    var loadScripts = function(srcs, callback) {
        (function(){
            if (srcs.length > 0) {
                loadScript(srcs.shift(), arguments.callee);
            } else {
                callback && callback();
            }
        }());
    }

    loadScript('/_resources/goog/base.js', function() {
        goog.require = function() {};

        $.ajax('/requires', {'data': {'modules': ['shegon.repl']},
                             'type': 'post',
                             'dataType': 'jsonp'})
         .done(function(data) {
            var srcs = $.map(data, function(module) {
                return module.url;
            });
            loadScripts(srcs);
         });
    });

});
