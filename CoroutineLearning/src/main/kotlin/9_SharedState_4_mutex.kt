package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

private val mutex = Mutex()
private var counter = 0
fun main() = runBlocking {

    val timeTaken = measureTimeMillis {
        massiveRun {
            mutex.withLock {
                counter++
            }
        }
        println(counter) // 100000
    }

    println("time taken for mutex= $timeTaken")
}

/*
* A Mutex (Mutual Exclusion) is a synchronization primitive used to protect critical sections — code that accesses shared
* mutable state. Only one coroutine or thread can hold the mutex at a time.
*
* You can imagine it as a room with a single key (or maybe a toilet at a cafeteria). Its most important function is lock.
* When the first coroutine calls it, it kind of takes the key and passes through lock without suspension. If another
* coroutine then calls lock, it will be suspended until the first coroutine calls unlock (like a person waiting for a key to the toilet).
* If another coroutine reaches the lock function, it is suspended and put in a queue, just after the second coroutine.
* When the first coroutine finally calls the unlock function, it gives back the key, so the second coroutine (the first one
* in the queue) is now resumed and can finally pass through the lock function. Thus, only one coroutine will be between lock and unlock.
*
*
*  Cons of Using Mutex
Disadvantage	                                    Explanation
❌ Not reentrant	                                A coroutine can deadlock if it tries to acquire the same mutex twice without releasing.
❌ Still needs discipline	                        If you forget to unlock() manually, you can get stuck forever (use withLock to avoid this).
❌ Slower than atomics for very simple operations	If you're only incrementing a number, AtomicInt is much faster and non-blocking.
❌ More overhead	                                Context switches and suspension make it less performant than lock-free algorithms.
* */