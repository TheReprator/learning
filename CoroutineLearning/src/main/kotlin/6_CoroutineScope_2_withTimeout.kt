package org.example

import kotlinx.coroutines.*

/*
    Coroutine builders                              Coroutine scope functions
    (except for runBlocking)

1)  launch, async, produce                          coroutineScope, supervisorScope, withContext, withTimeout

2)  Are extension functions on CoroutineScope       Are suspending functions.

3)  Take coroutine context from CoroutineScope      Take coroutine context from suspending function continuation.
    receiver.

4)  Exceptions are propagated to the parent         Exceptions are thrown in the same way as they are from/by
    through Job.                                    regular functions.


5)  Starts an asynchronous coroutine.               Starts a coroutine that is called in-place.

 */

/*
* withTimeout:- It also creates a scope and returns a value. Actually, withTimeout with a very big timeout behaves
* just like coroutineScope. The difference is that withTimeout additionally sets a time limit for its body execution.
* If it takes too long, it cancels this body and throws TimeoutCancellationException (a subtype of CancellationException).
* */


private suspend fun withTimeoutSampleType1(): Int = withTimeout(1500) {
    delay(1000)
    println("withTimeoutExecutionType1: Still thinking")
    delay(1000)
    println("withTimeoutExecutionType1: Done!")
    42
}
private suspend fun withTimeoutExecutionType1(): Unit= coroutineScope {
    try {
        withTimeoutSampleType1()
    } catch (e: TimeoutCancellationException) {
        println("withTimeoutExecutionType1: Cancelled")
    }
    delay(1000) // Extra timeout does not help,
    // `test` body was cancelled
}
/*
* As you remember: 5_ExceptionHandling_8_subclassException from example, whenever an exception is thrown that is child
* of CancellationException, it cancels only the current coroutine scope and will not propagate to parent.
*
* So, here when withTimeout(line 34) is not able to complete it's body within defined time of 15 seconds, it throw
* an exception(line 41) that is child of CancellationException, which can be caught with try block(line 39).
* So even applying a delay(line 44) won't help here as the function is already done means cancelled.
*
* */

private suspend fun withTimeoutExecutionType2(): Unit= coroutineScope {
    launch { // 1
        launch { // 2, cancelled by its parent
            delay(2000)
            println("withTimeoutExecutionType2: Will not be printed")
        }
        withTimeout(1000) { // will cancel launch
            delay(1500)
        }
    }
    launch { // 3
        delay(2000)
        println("withTimeoutExecutionType2: Done")
    }
}

/*
* Here an exception is thrown(line 63), as it has not completed its body withing 1 second, so will throw the exception
* TimeoutCancellationException (a subtype of CancellationException), which only results in cancellation of current
* coroutine scope(line 58) and will not affect the others(line 68)
* */


/*
* withTimeoutOrNull: It is a less aggressive variant of withTimeout, which does not throw an exception.
* If the timeout is exceeded, it just cancels its body and returns null.
* */

private suspend fun withTimeoutExecutionType3(): Unit= coroutineScope {
    launch { // 1
        launch {
            delay(2000)
            println("withTimeoutExecutionType3: Will be printed")
        }
        withTimeoutOrNull(1000) {
            delay(1500)
            println("withTimeoutExecutionType3: Will not be printed")
        }
    }
    launch { // 3
        delay(2000)
        println("withTimeoutExecutionType3: Done")
    }
}

/*
* Here, withTimeoutOrNull(line 91) will not be able to complete it's body with a limit of 1 second, so it simply
* returns null from there, its body. Will not affect the other coroutines any way, as no exceptions are thrown from
* there
* */


fun main(): Unit= runBlocking {
    withTimeoutExecutionType1()
    println()
    println()
    println()
    withTimeoutExecutionType2()
    println()
    println()
    println()
    withTimeoutExecutionType3()
}