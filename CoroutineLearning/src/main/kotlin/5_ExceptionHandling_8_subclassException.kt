package org.example

import kotlinx.coroutines.*

object MyNonPropogationException: CancellationException()

fun main(): Unit = runBlocking {
   launch {
       launch {
           delay(1000)
           println("I am child")
       }

       throw MyNonPropogationException
   }

    launch {
        delay(1100)
        println("I am child2")
    }
}

/*
* If an exception is a subclass of CancellableException, it will not be propagated to parent. It will only cause
* the cancellation of current coroutine
*
* At line 24, MyNonPropogationException exception is throw, but since MyNonPropogationException is child of CancellationException,
* It will only cause the current builder(line 8) to be cancelled. So, builder (line 17) will be untouched, as exception
* is never to parent(line 7)
* */