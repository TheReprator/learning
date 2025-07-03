package org.example

import kotlinx.coroutines.*

private suspend fun type1(): Unit = coroutineScope {
    supervisorScope {
        launch {
            delay(1000)
            throw Exception("SupervisorScope child Exception")
        }

        launch {
            delay(1500)
            println("SupervisorScope other child")
        }

        launch {
            delay(2000)
            println("SupervisorScope coroutine builder")
        }
    }

    launch {
        delay(800)
        println("SupervisorScope other child 0")
    }

    launch {
        delay(1400)
        println("SupervisorScope other child 1")
    }
    launch {
        println("SupervisorScope other child 2")
    }
}

/*
* supervisorScope: It is a coroutine scope function, so it behaves just like coroutineScope, but it uses a
* SupervisorJob instead of a regular Job
*
* So, it will behave the same way as SupervisorJob. As it will now catch the exception from child coroutine(line 9)
* and would not all the exception(line 9) to propagate to the other child builder(line 12, 17)
* */

fun main(): Unit = runBlocking {
    type1()
    println()
    println()
    println()
    //type2()
}

private suspend fun type2(): Unit = coroutineScope {
    supervisorScope {
        launch {
            delay(700)
            println("SupervisorScope type2:  inner child 1")
            launch {
                delay(1000)
                throw Exception("SupervisorScope type2: child Exception 2")
                launch {
                    println("SupervisorScope type2: inner inner child will not be executed 3")
                }
            }

            launch {
                delay(1200)
                println("SupervisorScope type2: inner child 4")
            }
        }
        launch {
            delay(1500)
            println("SupervisorScope type2: other child 5")
        }

        launch {
            delay(2000)
            println("SupervisorScope type2: coroutine builder 6")
        }
    }
}

/*
* If an exception occurs in a child of a child(line 47), it cancel itself(line 45) and its child(line 48),
* it propagates to the parent of this child(line 42), destroys it(line 42) and all it's subsequent child(line 53),
* and only then gets ignored.
* */