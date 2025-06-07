package org.example.creational

class Burger(
    val patty: String,
    val cheese: Boolean,
    val toppings: List<String>
) {
    override fun toString(): String {
        return "Burger with $patty, ${if (cheese) "cheese" else "no cheese"}, toppings: $toppings"
    }
}

class BurgerBuilder {
    private var patty: String = ""
    private var cheese: Boolean = false
    private val toppings: MutableList<String> = mutableListOf()

    fun setPatty(patty: String) = apply { this.patty = patty }
    fun addCheese() = apply { this.cheese = true }
    fun addTopping(topping: String) = apply { toppings.add(topping) }
    fun build() = Burger(patty, cheese, toppings)
}

fun main(args: Array<String>) {
    val burger = BurgerBuilder().setPatty("chicken")
        .addCheese()
        .addTopping("lettuce")
        .addTopping("tomato")
        .build()
    println(burger)
}