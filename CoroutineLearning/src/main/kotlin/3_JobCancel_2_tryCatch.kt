package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

/*
* When a job is cancelled, it changes its state to cancelling. Then at the first suspension point(line 18)
* a CancellationException is thrown, which can be caught using try catch, but is recommended to throw it
*
* But if line 19 is commmented, then job wil not cancel itself, as it have no suspension point and complete
* the loop till 1000
*
* */
suspend fun main() = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            repeat(1000) { i ->
                delay(200L)
                println("printing: $i")
            }
        }catch (e: CancellationException) {
            println("e ${e.localizedMessage}")
            throw e
        }
    }

    delay(1100L)
    job.cancelAndJoin()
    println("Cancelled! successfully")
}