package org.example.creational

private object President {
    init {
        println("Electing new president.")
    }
}

fun main() {
    val pres1 = President
    val pres2 = President
    println(pres1 === pres2) // true
}