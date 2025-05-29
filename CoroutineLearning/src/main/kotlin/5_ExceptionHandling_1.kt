package org.example

import kotlinx.coroutines.*

private suspend fun type1() = coroutineScope {
    launch {
        launch {
            delay(1000)
            throw Exception("type1: An error occurred")
        }

        launch {
            delay(1500)
            println("type1: Exception parent will not be printed")
        }

        launch {
            delay(500)
            println("type1: Exception parent child will be printed")
        }
    }

    launch {
        delay(2000)
        println("type1: Exception parent other child will be not printed")
    }
}

/*
* Once a coroutine receives an exception(line 9), it cancel itself(line 7) and propagates the exception to its
* parent(line 6). The parent(line 6) cancel itself and all its children(line 12 not line 17, as it is already
* completed by that time), then it propagates the exception to its parent(coroutineScope, line 5)., which again,
* then cancel itself and its children(line 23), then it propagates the exception to it's parent(runblocking line 38),
* Since, runblocking is a root coroutine(that has no parent), So it ends the program here, after cancelling its
* child coroutine (line 41), else it also go to its parent
* */

fun main(): Unit = runBlocking {
    type1()

    launch {
        delay(2100)
        println("runBlocking parent will be printed")
    }
}