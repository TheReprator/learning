package org.example.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    coroutineScope {
        launch {
            delay(100L)
            println("S")
        }
        delay(10L)
        println("O")
        launch {
            delay(500L)
            println("P")
        }
        coroutineScope {
            launch {
                delay(1L)
                println("B")
            }
            delay(1000L)
            println("C")
            launch {
                delay(10L)
                println("A")
            }
        }
    }
    println("D")
}

/*
DOBSPCA
* */