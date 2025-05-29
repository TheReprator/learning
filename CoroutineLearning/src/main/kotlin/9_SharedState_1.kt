package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.system.measureTimeMillis

private var counter = 0
private fun problemStatement() = runBlocking {
    massiveRun {
        counter++
    }
    println(counter) // ~65647
}

suspend fun massiveRun(action: suspend () -> Unit) =
    withContext(Dispatchers.Default) {
        repeat(100) {
            launch {
                repeat(1000) { action() }
            }
        }
    }

/*
The result is not 1,00,000 because the counter++ operation is not thread-safe.

Here's what’s happening:
Your code creates 100 coroutines, each of which increments the counter 1000 times — so ideally,
you expect 100 × 1000 = 1,00,000 increments.

However, counter++ is a compound operation, which involves:
Reading the current value.
Incrementing it.
Writing the new value back.

When multiple coroutines run concurrently on Dispatchers.Default, they may interleave these steps,
leading to race conditions. This means some increments are lost because two or more coroutines read the same value
before either writes back the incremented result.
* */

private fun synchronized() = runBlocking {
    val lock = Any()
    massiveRun {
        synchronized(lock) { // We are blocking threads!
            counter++
        }
    }
    println("Counter = $counter") // 1,00,000
}

/*
synchronized is a blocking lock that ensures only one thread at a time can execute a specific block of code.
It's like putting a "Do Not Disturb" sign on a shared resource — if Thread A is inside the block, all other threads
have to wait until it's done.

* This solution works, but there are a few problems.
*   The biggest one is, that inside synchronized block you cannot use suspending functions.
*   The second one is, that this block is blocking threads when a coroutine is waiting for its turn.
* */

fun main() = runBlocking {
    println("Problem statement")
    problemStatement()
    println()
    println()
    counter = 0
    println("Counter reset to $counter for race condition with synchronized")

    val measureTime = measureTimeMillis {
        synchronized()
    }
    println("time taken for synchronized= $measureTime")
}
