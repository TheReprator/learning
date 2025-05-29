package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() = coroutineScope {
     val job = launch {
        repeat(1000) { i ->
            delay(200L)
            println("printing: $i")
        }
    }

    delay(1000L)
    job.cancel()
    job.join()
    launch(job) {
        println("will not execute, as job is already cancelled!")
    }
    println("Cancelled! successfully")
}

/*
* Once the job is cancelled, it can't be used for any purpose.
* Once coroutine receive the cancel order(line 16), it instantly cancel all his child(line 9) and
* refrain from taking any further order(line no 18)
* */