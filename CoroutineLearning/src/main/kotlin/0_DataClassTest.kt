package org.example

data class DataClassTest(val id: Int, var age: Int = 0) {
    lateinit var name: String

    init {
        println("1 Initializing")
    }

    init {
        println("1 Initializing second")
    }

    constructor(age: Int): this(0, age) {
        println("2 secndry constructor")
    }

}

fun main(){
    println("First object primary")
    val dataClassPrimary = DataClassTest(1,2)
    println(dataClassPrimary)

    println("First object secondary")
    val dataClassSecondary = DataClassTest(1)
    println(dataClassSecondary)

    println("First object primary copy")
    val copyPrimary = dataClassPrimary.copy(age = 2)
    println(copyPrimary)

    println("First object secondary copy")
    val copySecondary = dataClassSecondary.copy(id = 2)
    println(copySecondary)
}