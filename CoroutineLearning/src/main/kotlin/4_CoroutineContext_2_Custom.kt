package org.example

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CustomCoroutineContext(private val name: String) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key

    private var nextNumber = 0

    fun printNextNumber() {
        println("$name $nextNumber")
        nextNumber++
    }

    companion object Key : CoroutineContext.Key<CustomCoroutineContext>
}

suspend fun printNextNumber() {
    coroutineContext[CustomCoroutineContext]?.printNextNumber()
}

suspend fun main(): Unit = withContext(CustomCoroutineContext("Counter Root")) {
    println("Root")
    printNextNumber()
    launch {
        println("Root 1")
        printNextNumber()
        println("Root 2")
        launch {
            println("Root 3")
            printNextNumber()
            println("Root 4")
        }

        println("Root 5")
        launch(CustomCoroutineContext("Counter Inner")) {
            println("Root 6")
            printNextNumber()
            printNextNumber()

            launch {
                println("Root 7")
                printNextNumber()
                println("Root 8")
            }
        }

        println("Root 9")
        printNextNumber()
    }
}