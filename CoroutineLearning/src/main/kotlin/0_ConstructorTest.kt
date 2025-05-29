package org.example

data class ConstructorTest (val id: Int) {
    lateinit var name: String
}

fun main() {
    val constructorTest = ConstructorTest(1)
    val constructorTestSecond = ConstructorTest(1)

    constructorTest.name = "vikram"
    val constructorTestCopy = constructorTest.copy()
    constructorTest.name = "vikram1"
    println(constructorTest == constructorTestCopy)
    println(constructorTest == constructorTestSecond)
    println(constructorTestCopy == constructorTestSecond)
}