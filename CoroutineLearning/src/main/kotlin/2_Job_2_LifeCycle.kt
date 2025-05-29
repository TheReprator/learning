package org.example

import kotlinx.coroutines.*

fun main() = runBlocking {

    val job = Job()
    println(job)
    job.complete()
    println(job)

    println()
    println()

    val activeJob = launch {
        delay(1000)
    }
    println(activeJob)
    activeJob.join()
    println(activeJob)

    println()
    println()

    val lazyJob = launch(start = CoroutineStart.LAZY) {
        delay(1000)
    }
    println(lazyJob)
    lazyJob.start()
    println(lazyJob)
    lazyJob.join()
    println(lazyJob)
}