package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

suspend fun main() = coroutineScope {
    repeat(1000) {
        launch { // or launch(Dispatchers.Default) {
            // To make it busy
            List(1000) { Random.nextLong() }.maxOrNull()
            val threadName = Thread.currentThread().name
            println("Running on thread: $threadName")
        }
    }
}

/*
Dispatchers: It is basically a mechanism, which is letting us decide on which thread (or pool of threads) a coroutine
            should be running (starting and resuming).
                In Kotlin coroutines, CoroutineContext determines on which thread a certain coroutine will run.

Default dispatcher: If you don’t set any dispatcher, the one chosen by default is Dispatchers.Default, which is designed
    to run CPU-intensive operations. It has a pool of threads with a size equal to the number of cores in the machine.
        At least theoretically, this is the optimal number of threads, assuming you are using these threads efficiently,
     i.e., performing CPU-intensive calculations and not blocking them.

         Output:
            Running on thread: DefaultDispatcher-worker-1
            Running on thread: DefaultDispatcher-worker-5
            Running on thread: DefaultDispatcher-worker-8
            Running on thread: DefaultDispatcher-worker-4
            Running on thread: DefaultDispatcher-worker-3
            Running on thread: DefaultDispatcher-worker-4
            Running on thread: DefaultDispatcher-worker-5
            Running on thread: DefaultDispatcher-worker-6
            Running on thread: DefaultDispatcher-worker-3
            .....

          Explanation: I have 8 cores in my system, so there are 8 threads in the pool

     Notes:  runBlocking sets its own dispatcher if no other one is set; so, inside it, the Dispatcher.Default is not
        the one that is chosen automatically. So, if we used runBlocking instead of coroutineScope in the above
        example, all coroutines would be running on “main”.
* */