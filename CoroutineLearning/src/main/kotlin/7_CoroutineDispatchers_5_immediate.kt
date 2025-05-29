package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/*
Dispatchers.Main.immediate =
"If I'm already on the Main thread, then run the coroutine immediately without dispatching.
Otherwise, behave like normal Dispatchers.Main (post to Main thread queue)."

Points:
    Main.immediate avoids unnecessary posting and delay
    Main always posts even if you're already on Main thread (this adds micro-delay).

* */

suspend fun main(): Unit = runBlocking(Dispatchers.Main) {
    println("Outside coroutine - Thread: ${Thread.currentThread().name}")

    launch(Dispatchers.Main) {
        println("Launch Main - Thread: ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Main.immediate) {
        println("Launch Main.immediate - Thread: ${Thread.currentThread().name}")
    }
}

/*
    Output:
        Outside coroutine - Thread: main
        Launch Main.immediate - Thread: main
        Launch Main - Thread: main

Explanation:
    Main.immediate executes instantly.
    Main schedules itself to run later in the Main Looper queue.
* */