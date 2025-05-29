package org.example

import kotlinx.coroutines.*

private suspend fun coroutineExceptionHandlerType1(): Unit = coroutineScope {
   val handler = CoroutineExceptionHandler { _, exception ->
       println("coroutineExceptionHandlerType1: Caught $exception")
   }

    launch(handler) {
        delay(1000L)
        throw Exception("coroutineExceptionHandlerType1: Exception")
    }

    launch(handler) {
        delay(2000L)
        println("coroutineExceptionHandlerType1: child 1")
    }

}

/*
* CoroutineExceptionHandler is a context that can be used to define default behavior for all exceptions in a coroutine.
* It does not stop the exception propagating, but it can be used to define what should happen in the case of an
* uncaught exception (the default behavior is that exception stacktrace is printed).
*
* Here, line 12, exception is thrown, it cancel itself, and propagate the exception to parent line 5,
* which then cancel itself and all it child(line 15), then propagate the exception to caller main function.
*
* Usage of CoroutineExceptionHandler(line 6), is of no use here, as exception is propagated to parent(line 5) and hence
* not caught
* */


private suspend fun coroutineExceptionHandlerType2(): Unit = coroutineScope {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("coroutineExceptionHandlerType1: Caught $exception")
    }

    val scope = CoroutineScope(SupervisorJob() + handler)
    scope.launch {
        delay(1000L)
        throw Exception("coroutineExceptionHandlerType1: Exception")
    }

    scope.launch {
        delay(1200L)
        println("coroutineExceptionHandlerType1: child 1")
    }

    delay(1500L)
}

/*
* Due to usage of SupervisorJob in scope(line 40), whenever exception(line 43) is thrown, it is caught and since
* CoroutineExceptionHandler(line 36) is also part of scope(line 40), the exception is displayed there
* */

fun main() = runBlocking {
    //coroutineExceptionHandlerType1()
    coroutineExceptionHandlerType2()
}