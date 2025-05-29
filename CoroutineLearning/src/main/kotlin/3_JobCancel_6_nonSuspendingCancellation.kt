package org.example

import kotlinx.coroutines.*

private suspend fun nonSuspendingCancellation() = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(10) { i ->
            Thread.sleep(200L)
            println("printing $i")
        }
    }
    delay(1000L)
    println("job waiting child to complete")
    job.cancelAndJoin()
    println("Cancelled successfully")
}

/*
* As cancellation happens at suspension point.
* Since there is no suspension point in builder(line 7), builder(line 7) will not cancel itself, despite getting
* the call(line 15) to cancel itself. So, it will complete itself fully
*
* Thread.sleep(200L) is not a suspending function, so that's why coroutine did not get a chance to cancel itself
* */


private suspend fun nonSuspendingCancellationSuspension() = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(10) { i ->
            Thread.sleep(200L)
            yield()
            println("printing $i")
        }
    }
    delay(1000L)
    println("job waiting child to complete")
    job.cancelAndJoin()
    println("Cancelled successfully")
}

/*
* Yield(line 33): It is basically a suspension function, which suspends the coroutine and return the same instantly.
*      This provides an opportunity to coroutines to do whatever is needed during suspension(resuming, cancellation,
* changing the thread during dispatcher)
* */


private suspend fun nonSuspendingCancellationSuspensionIsActive() = coroutineScope {
    val job = Job()
    launch(job) {
        while (isActive) {
            Thread.sleep(200L)
            ensureActive()
            println("printing ")
        }
    }
    delay(1100)
    println("job waiting child to complete")
    job.cancelAndJoin()
    println("Cancelled successfully")
}

/*
* isActive(line 53): It is basically a property of job, which tells whether job is active or not
* */

private suspend fun nonSuspendingCancellationSuspensionEnsureActive() = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(50) { i ->
            Thread.sleep(200L)
            ensureActive()
            println("printing $i")
        }
    }
    delay(1100)
    println("job waiting child to complete")
    job.cancelAndJoin()
    println("Cancelled successfully")
}

/*
* ensureActive(line 74): It is basically a suspension function, which throws CacenllationException if job is not
* active. It is very lighter
*
* The result of ensureActive and Yield is very similar. But there are very different as ensureActive is called on
* Coroutine Scope while yield is a normal suspending function. Always use ensureActive, And use Yield in a cpu
* intensive task
* */

suspend fun main() = coroutineScope {
    nonSuspendingCancellation()
    println()
    println()
    println()
    nonSuspendingCancellationSuspension()
    println()
    println()
    println()
    nonSuspendingCancellationSuspensionIsActive()
    println()
    println()
    println()
    nonSuspendingCancellationSuspensionEnsureActive()
    println()
    println()
    println()
}