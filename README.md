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
        Assign().From(Var('arg1')).To(Var('temp').End().
        If().Condition(Var('temp').
            Then().
                Assign().Value(Var('arg1').To(Var('Result')).End().
            End().
            Else().
                Assign().Value(Var('arg2').To(Var('Result')).End().
            End().
        End().
        WriteLine().Text('end').End().
    End().
End();
```
