# What I discovered with Thread interrupt and life cycle

## Thread life cycle

It's easy to start a Thread, Runnable increase the facility to start task.
However, stop a thread is a little bit more tricky, calling `.interupt()` doesn't stop the thread.
It just set the Thread internal flag `interupt` at true.

To stop correctly thread, I discovered an Exception : `InterruptedException` .
When interrupt is called, the exception is triggered and now its possible to stop the Thread

Note : Nested Thread is more complicated, example :
If you add a Thread inside the Runnable executed by the basic thread, you have one thread inside the thread you want to manage.
When Interrupt is called, the thread you wan't to will not be stopped. Because the nested thread is processing.


## Threads and executors

Executors doesn't solve stopping Runnable who's doing `while(true)`, a workaround exist by adding in the main loop a Thread.sleep(0).
When `ex.shundownNow()` is triggered, the  `InterruptedException` is triggered, we can catch the exception and break the loop.
Without the `Thread.sleep(0)` the runnable will never be stopped.


## Runnable and Thread -

Runnable is mainly a class with an abstract function `run()` method. thank to modern Java we can write runnable as lambda with the notation :
```java
(() ->{
        // run() function content
})
```

But in our TP how stop a runnable ? If we plan to schedule task inside an `Executor` and this task finis, how to say "Hey ?! please ? I finised!"

The first experience I made is that, use a second task executor in charge of monitoring if the Executor containing the  `while(true)` runnable has finished.
But remember we can't force the a Thread to stop without the `InterruptedException`, so I cheated by adding a `Thread.sleep(0)` inside the mainloop :) ....

So the code looks like this
```java
(() -> {
      while (true) {
        ;
        try {
          Thread.sleep(0);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      })
```
The magic is around throw the exception, that breaks the loop and finish the loop.
_Note that we can remove the exception throw and replace it by a simple break; here the objective is to end the loop._

### Why ? Do we need this exception to stop this runnable?

First of all, how sleep works ?
```java
  long nanos = MILLISECONDS.toNanos(millis);
  ThreadSleepEvent event = beforeSleep(nanos);
  try {
      if (currentThread() instanceof VirtualThread vthread) {
          vthread.sleepNanos(nanos);
      } else {
          sleep0(nanos);
      }
  } finally {
      afterSleep(event);
  }
```

In our experiment we aren't using VirtualThread, so sleep0 will be called.
Here the `sleep0()` code ...
```java
    private static native void sleep0(long nanos) throws InterruptedException;
```
and that's all...

what's meaning `native` keyword??
Here a detailed answer : https://stackoverflow.com/questions/6101311/what-is-the-native-keyword-in-java-for

What did I understand? ==> java, call the JNI sleep function of the os where java is executed.
Ok but ? What and how `InterruptedException` is raised ?


### How interrupt() works really ?

For interrupt a thread we need to reach one of these three conditions.
- the Thread is locked **blocked** buu and invocation of `wait()`, `join()`, `sleep()`
- the Thread is blocked in an I/O operation upon an java.nio.channels.InterruptibleChannel
- the thread is blocked in a NIO Selector

If **none** of the previous conditions hold then this thread's interrupt status will be set.

```java
/* The object in which this thread is blocked in an interruptible I/O
 * operation, if any.  The blocker's interrupt method should be invoked
 * after setting this thread's interrupt status.
 */
volatile Interruptible nioBlocker;

/* Set the blocker field; invoked via jdk.internal.access.SharedSecrets
 * from java.nio code
 */
static void blockedOn(Interruptible b) {
        Thread me = Thread.currentThread();
        synchronized (me.interruptLock) {
            me.nioBlocker = b;
        }
}

public void interrupt() {
        if (this != Thread.currentThread()) {
        checkAccess();
 // thread may be blocked in an I/O operation
        synchronized (interruptLock) {
            Interruptible b = nioBlocker;
            if (b != null) {
                interrupted = true;
                interrupt0();  // inform VM of interrupt
                b.interrupt(this);
                return;
            }
        }
    }
    interrupted = true;
    interrupt0();  // inform VM of interrupt
    }


private native void interrupt0(); // <------- JNI call
```

When Thread call external blocking sequence (I/O or channel) a nioBlocker is set.
`Interruptible` is set by `blockedOn()` function.
When `interrupt()` is called, verification are done and `interrupt0()` is raised.


### How interrupt0() works really ?

After being lost, I found some infos on : https://openjdk.org/ and Carl's Blog

Here the code executed for interrupt0()
```c
JVM_ENTRY(void, JVM_Interrupt(JNIEnv* env, jobject jthread))
  ThreadsListHandle tlh(thread);
  JavaThread* receiver = NULL;
  bool is_alive = tlh.cv_internal_thread_to_JavaThread(jthread, &receiver, NULL);
  if (is_alive) {
    Thread::interrupt(receiver);
  }
JVM_END
```
we can see an `cv_internal_thread_to_JavaThread(**)` and `Thread::interrupt(receiver);`
I suppose, the read thread is found and `receiver` is a reference to thread context used to force interrupt it.

```c
 void Thread::interrupt(Thread* thread) {
  os::interrupt(thread);
}

void os::interrupt(Thread* thread) {
  OSThread* osthread = thread->osthread();
  if (!osthread->interrupted()) {
    osthread->set_interrupted(true);
    ParkEvent * const slp = thread->_SleepEvent ;
    if (slp != NULL) slp->unpark() ;
  }
  ((JavaThread*)thread)->parker()->unpark();
  ParkEvent * ev = thread->_ParkEvent ;
  if (ev != NULL) ev->unpark() ;
}
```

We can see here, if the `interrupted` flag is not set, it is set it true. After that the Thread is waked up by unpark it and let it finish the job.


## Conclusion
Basically I would like to understand low level of threading, by implementing a basic heartbeat.
What's happens? I started by declaring a Thread, with an infinite loop (real big mistake...). I'm stuck here :D
Impossible to manage the life cycle of a Thread running an infinite loop... :D never blocked, never interrupted :D

So to understand, I did this research... And today... except kill the thread in the jvm directly (with rust JNI :o ?)
**I don't know how to do that!**


## Ressources
- https://martinfowler.com/articles/patterns-of-distributed-systems/heartbeat.html
- https://blog.teemo.co/vertx-in-production-d5ca9e89d7c6
- https://carlmastrangelo.com/blog/javas-mysterious-interrupt
- https://www.baeldung.com/java-lang-thread-state-waiting-parking#parking-and-unparking-threads
- https://hg.openjdk.org/jdk/jdk/file/1871c5d07caf/src/java.base/share/native/libjava/Thread.c#l54
- https://hg.openjdk.org/jdk/jdk/file/1871c5d07caf/src/hotspot/share/prims/jvm.cpp#l3108
