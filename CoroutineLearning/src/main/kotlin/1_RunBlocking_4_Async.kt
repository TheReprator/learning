package org.example

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    type()
    //typeAsynch()
}

private suspend fun type() = runBlocking {
    val asyncNotLazy = GlobalScope.async {
        println("inside async not lazy")
        422343
    }


    val asyncLazy = GlobalScope.async(start = CoroutineStart.LAZY) {
        println("inside async lazy")
        42
    }

    println("Async lazy")


    val asyncLazyImmediate = GlobalScope.async(start = CoroutineStart.LAZY) {
        println("inside async lazy immediate")
        42
    }

    println("Async lazy immediate")
    val asyncLazyImmediateResult = asyncLazyImmediate.await()
    println(asyncLazyImmediateResult)
    println()
    println()

    val asyncJob = GlobalScope.async {
        println("inside async")
        42
    }

    println("Async without delay")
    val asyncJobResult = asyncJob.await()
    println(asyncJobResult)
    println()
    println()


    val asyncJob1 = GlobalScope.async {
        println("inside async delay")
        delay(1000L)
        println("inside async delay after delay")
        40
    }

    println("Async with delay")
    val asyncJobResult1 = asyncJob1.await()
    println(asyncJobResult1)
}


private suspend fun typeAsynch() = runBlocking {
    async(start = CoroutineStart.LAZY) {
        println("inside async not lazy")
        422343
    }
}
/*
* Whenever we use async(like line 62), the program get's in an infinite loop of suspension,
* Bug: https://github.com/Kotlin/kotlinx.coroutines/issues/1065
*
* But with line 17( GlobalScope.async(start = CoroutineStart.LAZY)), it's good
* */