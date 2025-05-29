package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.system.measureTimeMillis

private val semaphore = Semaphore(1)
private var counter = 0
fun main() = runBlocking {

    val timeTaken = measureTimeMillis {
        massiveRun {
            semaphore.withPermit {
                counter++
            }
        }
        println(counter) // 100000
    }

    println("time taken for mutex= $timeTaken")
}

/*
A semaphore is a concurrency mechanism that controls access to a resource by maintaining a counter:
    The counter represents available "permits".
    Each time a thread/coroutine acquires a permit, the counter decreases.
    When it releases a permit, the counter increases.

🔁 Real-life analogy
    Imagine a parking lot with 3 spots:
        Only 3 cars can park (i.e. access a resource) at a time.
        If the lot is full, incoming cars wait (suspend) until someone leaves.

✅ Pros of Semaphores
    Advantage	                    Explanation
    ✅ Allows multiple access	    Unlike Mutex (1 at a time), semaphores can allow N concurrent accesses.
    ✅ Coroutine-friendly	        Kotlin's coroutine Semaphore suspends instead of blocking.
    ✅ Fairness	                    Optional fairness mode ensures FIFO access (with Java's semaphores).
    ✅ Great for rate-limiting	    Useful in limiting API calls, DB connections, or thread pools.

❌ Cons of Semaphores
    Disadvantage	                Explanation
    ❌ Manual management	        You must manually acquire and release permits — misuse can lead to resource leaks.
    ❌ Harder to reason about	    Logic is more abstract compared to mutexes or locks.
    ❌ Deadlock risk	            Forgetting to release a permit can cause permanent waiting.
    ❌ Not reentrant	            Like Mutex, reacquiring a held permit in the same coroutine can deadlock.

🛠️ Use Cases
    🌐 Rate limiting (e.g., limit API calls to 5/sec)
    🧵 Thread pool throttling
    🔁 Bounded resources (e.g., database connections)
    🗂️ Controlled parallel processing
* */