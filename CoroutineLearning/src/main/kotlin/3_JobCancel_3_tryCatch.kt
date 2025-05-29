package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

suspend fun main() = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("Job is done")
        } finally {
            println("finally")
            launch {
                println("will not be printed")
            }
            try {
                delay(1000)
            } catch (e: CancellationException) {
                println("job is already cancelled, no suspension allowed")
                throw e
            }
            println("will not be printed 1")
        }
    }

    delay(1000L)
    job.cancelAndJoin()
    println("Cancel done")
}

/*
* Once a coroutine is cancelled(line 28), it can run as long as there is no suspension point(line 10)
* Once cancelled, suspension or builder is no longer allowed(line 14,18),
* if we try to suspend(line 18), it will throw cancellation exception
*
* If we try to start another coroutine, it will be ignored(line 14)
*
* */