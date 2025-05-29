package org.example

import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    type1()
    println()
    println()
    type2()
}

private suspend fun type1()= coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("coroutine finished")
        } finally {
            println("finally")
            withContext(NonCancellable) {
                delay(1000L)
                println("cleanup now")
            }
            println("finally 1")
        }
    }

    delay(1000L)
    job.cancelAndJoin()
    println("Cancel done")
}
/*
* NonCancellable(line 18): It is basically a job that cannot be cancelled and is
* active in state. We can call whatever suspending function(21),  we want inside it
* */

private suspend fun type2() = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            delay(2000)
            println("coroutine finished")
        } finally {
            println("finally")
            withContext(NonCancellable) {
                delay(1000L)
                println("cleanup now")
                try {
                    coroutineContext[Job]?.cancelAndJoin()
                } catch (e: Exception) {
                    println("job cancelled catch")
                    throw e
                }
            }
            println("finally 1")
        }
    }

    delay(1000L)
    job.cancelAndJoin()
    println("Cancel done")
}

/*
Here line 55, is never printed because the active non canellable job(line 45) is being cancelled in line 49.
It terminate abnormally, depending on the internal coroutine implementation.
* */