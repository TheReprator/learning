package org.example

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.repeat

/*
 What is a Flow in Kotlin?
    Flow = A cold, asynchronous stream of multiple values, using coroutines.
    ✅ Think of it like a List, but values arrive over time, not all at once.
    ✅ Flow uses suspend functions internally.
    ✅ It's like a "pipe" that emits values when ready.

    Flow is a lazy, asynchronous, suspendable stream of multiple values, using coroutines — ideal for async work
    like network, database, user inputs, and timer events.

 Core Concepts of Flow
    Cold Stream (does nothing until collected)
    Emit Values one by one (emit(x))
    Suspending functions inside flow (can delay, apiCall(), etc.)
    Thread control (flowOn())
    Operators (map, filter, transform, etc.)
    Backpressure handled automatically
    Exception handling (catch {})

📦 Full Architecture of a Flow
    Inside Flow, three important stages:

        Builder (Producer)
            flow {}, flowOf(), asFlow()
            Code that emits values

        Intermediate Operators
            map, filter, take, transform, debounce, buffer, onEach, take(n), catch, etc.
            Modify flow without collecting yet.

        Terminal Operator (Collector)
            collect {}, toList(), first()
            Actually starts the flow.
    👉 Flow does NOTHING until collected.

    🎯 Why is Flow Cold?
    Because:
        When you define flow {} — nothing happens.
        When you call collect — the flow block starts.

    🛠 Final Mind Map of Flow
                +------------+
                |   Flow     |
                +------------+
                      |
        +----------------------------+
        |                            |
   Build (flow, flowOf, asFlow)     Operators (map, filter, catch)
                      |
                  Collect (collect{}, toList{})

Flow.collect is a suspending function — it blocks the coroutine until complete, but it does not block the underlying
thread unless you use runBlocking.

It is the terminal operators that is suspending and builds a relation to its parent coroutine (similar to
the coroutineScope function).
* */

/*
Notice, that this function is not suspending and does not need CoroutineScope. As only flow terminal operators are
suspending, so whenever we apply terminal operators, it needs to be in coroutine scope
 */
private fun usersFlow(): Flow<String> = flow {
    repeat(3) {
        delay(1000)
        val ctx = currentCoroutineContext()
        val name = ctx[CoroutineName]?.name
        emit("User$it in $name")
    }
}

private fun flow_type1(): Unit = runBlocking {
    val users = usersFlow()
    withContext(CoroutineName("Name")) {
        val job = launch {
            users.collect { println(it) }
            println("Done! vvv")
        }
        launch {
            delay(2100)
            println("I got enough")
            //job.cancel()
        }
    }
}

/*
* Output:
    User0 in Name
    User1 in Name
    I got enough
    User2 in Name
    Done! vvv

 Here you can see "Done! vvv" is printed at last in the coroutine 87, which proves are statement(line 64-65)

* */

fun flow_type2(): Unit = runBlocking {
    val coldFlow = flow {
        println("Flow started")
        emit(1)
        emit(2)
    }
    coldFlow.collect { println("Collector 1: $it") }
    coldFlow.collect { println("Collector 2: $it") }
}

/*
Output:
    Flow started
    Collector 1: 1
    Collector 1: 2
    Flow started
    Collector 2: 1
    Collector 2: 2

Observation:
    Cold flow restarts for each collector
* */

fun main(): Unit = runBlocking {
    flow_type1()
    println()
    println()
    flow_type2()
}



