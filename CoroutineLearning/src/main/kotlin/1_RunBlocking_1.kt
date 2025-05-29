package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        delay(1000L)
        println("World!")
    }
    runBlocking {
        delay(1000L)
        println("1 World!")
    }

    runBlocking {
        delay(1000L)
        println("2 World!")
    }

    runBlocking {
        delay(1000L)
        println("3 World!")
    }

    println("Hello!")
}