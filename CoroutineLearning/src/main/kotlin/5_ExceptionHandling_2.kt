package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    type1()
    println()
    println()
    type2()
}

private suspend fun type1() = coroutineScope {
    launch {
        try {
            delay(1000)
            throw Exception("type1: An error occurred")
        } catch (ex: Exception) {
            println("type1: will not be printed")
        }
    }

    launch {
        delay(2000)
        println("type1: will not be printed22")
    }
}

private suspend fun type2() = coroutineScope {

    try {
        launch {
            delay(1000)
            throw Exception("type2: An error occurred")
        }
    } catch (ex: Exception) {
        println("type2: will not be printed")
    }

    launch {
        delay(2000)
        println("type2: will not be printed22")
    }
}

/*
* Catching an exception before it breaks, is helpful not after that i.e.
* like in type1(), we are catching the exception within the coroutine builder(line 17), so the exception in not thrown
* or propagated to parent
* but with type2(), try catch(line 33) is added outside builder, which is of no use, as when exception is thrown,
* it will be propagated or thrown to parent
* */