$(function() {

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
        var prompt = 'cljs.user>';
        var history = [];
        var historyPos = 0;
        var lastHistoryChange = null;
        var currentNotHistoricValue = '';

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



        var readEvalPrint = function() {
            var currentValue = getCurrentValue();

            history.push(currentValue);
            historyPos = 0;

            doEval(currentValue, function(result) {
                var last = lastPos(doc);
                doc.replaceRange('\n' + result + '\n\n', last, last);
                rePrompt();
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
                .done(function(data) { callback(catchingEval(data.result)); })
                .fail(function() { alert(2); });
        }

        var showHistory = function(delta) {
            if (!history.length) return;

            historyPos += delta;

            if (historyPos <= 0) {
                historyPos = 0;
            } else if (historyPos > history.length) {
                historyPos = history.length;
            }

            var historicValue = historyPos == 0 ? currentNotHistoricValue :
                                    history[history.length - historyPos];

            lastHistoryChange = historicValue;
            doc.replaceRange(historicValue, currentStart, lastPos(doc));
        }


    });

});
