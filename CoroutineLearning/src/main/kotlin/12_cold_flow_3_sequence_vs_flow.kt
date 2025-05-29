package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/*
    Major Differences
    Aspect                  | Sequence                              | Flow
    Threading               | Same thread only                      | Can change threads (Dispatchers)
    Laziness                | Lazy, pull-based                      | Lazy, push-based
    Suspending Functions    | ❌ Not allowed                        | ✅ Allowed (you can delay, network calls, etc.)
    Asynchrony              | ❌ Cannot wait / delay                | ✅ Can wait, delay, switch threads
    Error Handling          | Normal try-catch                      | catch {} operator available
    Performance             | Super lightweight (zero overhead)     | Slight overhead due to coroutines
    Use case                | In-memory, simple transformation      | Asynchronous, IO, timers, events

 Important Deep Note
    Sequence is pull-based:
        Consumer pulls elements (asks: "Give me next")
    Flow is push-based:
        Producer pushes elements when ready
* */

private fun simpleNumbers() = sequence {
    for (i in 1..3) {
        yield(i)
    }
}

private fun delayedNumbers() = flow {
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

fun main() = runBlocking {
    println("=== Sequence ===")
    simpleNumbers().forEach { println(it) }

    println()
    println()

    println("=== Flow ===")
    delayedNumbers().collect { println(it) }
}

/*
Since, sequence is like local finite machine, which does not support suspension, hence we can't use delay or another
coroutine functionality withing sequence

Output:
    === Sequence ===
    1
    2
    3

    === Flow ===
    1(after 1 sec)
    2(after 1 sec)
    3(after 1 sec)

* */