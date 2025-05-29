package org.example

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/*
* Dispatchers.IO
        Slow (waiting for response)
        Blocking (reading/writing files, network requests)
        Not CPU-intensive (you’re mostly waiting, not computing)

   Example activities:
        Accessing a Room database
        Making a Retrofit network call
        Reading from a file
        Writing to a log file

Use Dispatchers.IO for any task where the app waits for something external (like internet or disk)
and doesn’t compute much itself

Dispatchers.IO can handle up to 64 parallel threads by default — it's optimized for lots of waiting tasks,
not heavy CPU work. If you want to increase the limit to beyond 64, you have to use it like below,
    -Dkotlinx.coroutines.io.parallelism=128

    val customIODispatcher = Dispatchers.IO.limitedParallelism(128)
        Here, limitedParallelism(128) means allow up to 128 parallel threads instead of 64.
* */


private suspend fun type1() {
        coroutineScope {
            repeat(500) {
                launch(Dispatchers.IO) {
                    Thread.sleep(200)
                    val threadName = Thread.currentThread().name
                    println("Running on thread: $threadName")
                }
            }
    }
}

/*
Output:
        Running on thread: DefaultDispatcher-worker-6
        Running on thread: DefaultDispatcher-worker-3
        Running on thread: DefaultDispatcher-worker-1
        Running on thread: DefaultDispatcher-worker-2
        Running on thread: DefaultDispatcher-worker-27
        Running on thread: DefaultDispatcher-worker-4
        Running on thread: DefaultDispatcher-worker-45
        Running on thread: DefaultDispatcher-worker-5
        Running on thread: DefaultDispatcher-worker-59
        .....
 */

private suspend fun type2() = coroutineScope {
    launch(Dispatchers.Default) {
        println(Thread.currentThread().name)
        withContext(Dispatchers.IO) {
            println(Thread.currentThread().name)
        }
    }
}

/*
Dispatchers.Default and Dispatchers.IO share the same pool of threads.

Output:
        DefaultDispatcher-worker-1
        DefaultDispatcher-worker-1

For a clear picture, please find image in images/7_CoroutineDispatchers_3_io.png
* */

suspend fun main() {
    type1()
    println()
    println()
    println()
    println()
    type2()
}

