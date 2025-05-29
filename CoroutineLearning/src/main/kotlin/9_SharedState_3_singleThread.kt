package org.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

val dispatcher = Dispatchers.IO.limitedParallelism(1)

private var counter = 0

fun main() = runBlocking {
    val timeTaken = measureTimeMillis {
        massiveRun {
            withContext(dispatcher) {
                counter++
            }
        }
        println(counter) // 100000

    }

    println("time taken for single thread= $timeTaken")
}

/*
* Here with a dispatcher limited to a single thread(line 7). This solution is easy and eliminates conflicts, but the
* problem is that we lose the multithreading capabilities of the whole function
* */