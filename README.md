pacman
======

Tiny activity engine. Activities run like walking along the beans, enjoy it :)

you can read something about its design [here](https://github.com/ali-ent/NTFE-BPM/tree/master/doc)

```js
var wf = Workflow.
    In('arg1').
    In('arg2').
    Out('result').
    Var('temp').
    Sequence().
        Assign().From('arg1').To('temp').End().
        If().Condition('temp').
            Then().
                Assign().From('arg1').To('Result').End().
            End().
            Else().
                Assign().From('arg2').To('Result').End().
            End().
        End().
        WriteLine().Text('end').End().
    End().
End();
```
