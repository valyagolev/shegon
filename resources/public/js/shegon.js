$(function() {
    window.shegon = window.shegon || {};

    var Pos = function(line, ch) {
        return {'line': line, 'ch': ch};
    }

    var lastPos = function(doc) {
        var lastLine = doc.lineCount() - 1;
        return Pos(lastLine, doc.getLine(lastLine).length);
    };


    // if it's compiler page
    $('form.compiler-form').each(function() {
        var $form = $(this);
        var $txt = $('textarea', $form);
        CodeMirror.fromTextArea($txt[0],
                                {'matchBrackets': true,
                                 'extraKeys':
                                    {'Cmd-Enter':
                                        function(){
                                            $form.submit();
                                        }
                                    }
                                }).focus();
    });

    // if it's repl page
    $('textarea.repl').each(function() {

        var $txt = $(this);


        var doc = CodeMirror.fromTextArea($txt[0],
                                {'matchBrackets': true,
                                 'extraKeys':
                                    {'Enter': function() {
                                        readEvalPrint(); },
                                     'Up': function() {
                                        showHistory(1); },
                                     'Down': function() {
                                        showHistory(-1); }}
                                });

        doc.focus();

        var currentStart = null;
        var prompt = 'cljs.user=>';
        var currentNotHistoricValue = '';

        var history = (function() {

            if (!window.localStorage.evalHistory)
                window.localStorage.evalHistory = "[]";

            var actual = JSON.parse(window.localStorage.evalHistory);
            var pos = 0;

            var commit = function() {
                window.localStorage.evalHistory = JSON.stringify(actual);
            }

            return {
                'addLine': function(line) {
                    actual.push(line);
                    commit();
                    pos = 0;
                },
                'empty': function() {
                    return actual.length == 0;
                },
                'move': function(delta) {
                    pos += delta;

                    if (pos <= 0) {
                        pos = 0;
                    } else if (pos > actual.length) {
                        pos = actual.length;
                    }
                },
                'current': function() {
                    return pos == 0 ?
                            currentNotHistoricValue :
                                actual[actual.length - pos];
                }
            };

        }());

        var lastHistoryChange = null;


        var getCurrentValue = function() {
            if (!currentStart) return '';
            return doc.getRange(currentStart, lastPos(doc));
        }

        doc.on('change', function() {
            var currentValue = getCurrentValue();
            if (currentValue != lastHistoryChange) {
                historyPos = 0;
                currentNotHistoricValue = currentValue;
            }
        });

        var addNotReadOnlySpace = function (where) {
            notReadOnlyEnd = Pos(where.line, where.ch + 1);
            doc.replaceRange(' ', where, where);

            // doc.markText(where, notReadOnlyEnd, {'readOnly': false});

            return notReadOnlyEnd;
        }

        var moveStart = function(where) {
            currentStart = where;

            var newSpace = addNotReadOnlySpace(where);

            doc.markText({'line': 0, 'ch': 0}, currentStart,
                         {'atomic': true});

            doc.setCursor(newSpace);
        }

        var rePrompt = function() {
            var last = lastPos(doc);
            doc.replaceRange(prompt, last, last);
            moveStart(lastPos(doc));
        }

        rePrompt();


        var logToConsole = function(smth) {
            var last = lastPos(doc);

            doc.replaceRange('\n' + smth + '\n\n', last);

            rePrompt();
        }

        window.shegon.logToConsole = function(smth) {
            var currentValue = getCurrentValue().trim();

            logToConsole(smth);

            doc.replaceRange(currentValue, lastPos(doc));
        }

        var readEvalPrint = function() {
            var currentValue = getCurrentValue();

            history.addLine(currentValue);

            doEval(currentValue, function(result) {
                var str_result;
                try {
                    // this all because result may be too lazy
                    str_result = '' + result.evalResult;
                } catch (e) {
                    str_result = e.stack;
                }

                if (result.ns)
                    prompt = result.ns + '=>';

                logToConsole(str_result);
            });
        }

        var catchingEval = function(value) {
            try {
                return eval(value);
            } catch (e) {
                return e.stack;
            }
        }


        var doEval = function(value, callback) {
            return $
                .ajax('/compiler', {'type': 'post',
                                    'data': {'source': value},
                                    'dataType': 'jsonp'})
                .done(function(data) {
                    data.evalResult = catchingEval(data.result);
                    callback(data);
                })
                .fail(function() { alert(2); });
        }

        var showHistory = function(delta) {
            if (history.empty()) return;

            history.move(delta);

            var historicValue = history.current();

            lastHistoryChange = historicValue;
            doc.replaceRange(historicValue, currentStart, lastPos(doc));
        }


    });

});
