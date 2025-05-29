package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/*
Dispatchers.Unconfined: The coroutine starts in the current thread but can resume in any thread later

When you use:
    a)The coroutine starts immediately in the same thread that launched it.
    b)If the coroutine suspends (e.g., does delay(), await(), etc.) — then resumes possibly on another thread!
    ✅ No fixed thread or pool.
    ✅ It’s free — unbound to any particular thread

Start -> [ Current Thread ]
Pause -> [ Move to any available thread ]
Resume -> [ Some other thread ]

* */

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
suspend fun main(): Unit =
    withContext(newSingleThreadContext("Thread1")) {
        var continuation: Continuation<Unit>? = null
        launch(newSingleThreadContext("Thread2")) {
            delay(2000)
            continuation?.resume(Unit)
        }
        launch(Dispatchers.Unconfined) {
            println(Thread.currentThread().name) // Thread1
            suspendCancellableCoroutine<Unit> {
                println("callback suspension")
                continuation = it
            }
            println(Thread.currentThread().name) // Thread2
            delay(1000)
            println(Thread.currentThread().name)
        }
    }

/*
    Output:
        Thread1
        callback suspension
        (wait for 2 second)
        Thread2
        (wait for 1 second)
        kotlinx.coroutines.DefaultExecutor

Explanation:
    At line 30, it started with Unconfined dispatcher, prints the current thread a line 31, then it suspends due to
    suspendCancellableCoroutine(line 32), and after 2 second, it is resumed(line 28) in another coroutine and again
    suspend at line 37, then again resume at line 38, which proves our statement form line 16-18
* */