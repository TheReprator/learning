package org.example

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

private var counter = AtomicInteger()

fun main() = runBlocking {
    val timeTaken = measureTimeMillis {
        massiveRun {
            counter.incrementAndGet()
        }
        println(counter.get()) // 100000
    }
    println("time taken for AtomicInteger= $timeTaken")
}

/*
* Atomics provide lock-free, thread-safe operations on variables. Think of them as synchronized variables, but faster
* and without blocking other threads.
*
* They internally use low-level CPU instructions like CAS (Compare-And-Swap) to ensure that operations like
* incrementAndGet() happen atomically — meaning no thread can see an intermediate state.
*
* This solution works, but there are a few problems.
*   The biggest one is, Can’t atomically update multiple variables together
*   The second one is, Not suspendable, doesn't cooperate with structured concurrency (e.g. Kotlin coroutines)
* */