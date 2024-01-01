# What I discovered

## Thread life cycle

It's easy to start a Thread, Runnable increase the facility to start task.
However stop a thread is a little bit more tricky, calling `.interupt()` doesn't stop the thread.
It just set the the Thread internal flag `interupt` at true.

To stop correctly thread, I discovered an Exception : `InterruptedException` .
When interrupt is called, the exception is triggered and now its possible to stop the Thread

Note : Nested Thread is more complicated, example :
If you add a Thread inside the Runnable executed by the basic thread, you have one thread inside the thread you want to manage.
When Interrupt is called, the thread you wan't to will not be stopped. Because the nested thread is processing.

https://martinfowler.com/articles/patterns-of-distributed-systems/heartbeat.html
https://blog.teemo.co/vertx-in-production-d5ca9e89d7c6
