activator-cqrs-rest
===================

Activator that demonstrates the approaches to building REST interfaces around distributed CQRS applications

To see how it all runs, run the ``RouterMain`` once, then ``WriteMain`` and ``QueryMain`` as many times as you like. 
You can then experiment with the routing, making requests to ``http://localhost:8080/1.0.0/*``, which will be routed
to the appropriate _side_ with the _version_ element stripped out.

There is a convenient (but rather timing-dependant) ``runAll`` task, which runs ``RouterMain``, one ``WriteMain`` and 
several ``QueryMain``.

Thus, a GET request to ``http://localhost:8080/1.0.0/exercise`` will show

```json
[{
    "processName": "9688@Jans-Mac-Pro.local",
    "what": "gym::abs",
    "duration": 30
}, {
    "processName": "9688@Jans-Mac-Pro.local",
    "what": "cycle::intervals",
    "duration": 60
}, {
    "processName": "9688@Jans-Mac-Pro.local",
    "what": "cycle::hills",
    "duration": 124
}]
```

(Notice the ``processName`` attribute, which will change in a round-robin fashion between requests.)
