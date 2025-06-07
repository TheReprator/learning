package org.example.creational

interface Pizza {
    fun prepare(): String
}

class VegPizza : Pizza {
    override fun prepare() = "Preparing Veg Pizza"
}

class CheesePizza : Pizza {
    override fun prepare() = "Preparing Cheese Pizza"
}

//It is factory pattern
class PizzaStore {
    fun orderPizza(type: String): Pizza = when (type) {
        "veg" -> VegPizza()
        "cheese" -> CheesePizza()
        else -> throw IllegalArgumentException("Unknown pizza type")
    }
}

fun main() {
    val store = PizzaStore()
    val pizza = store.orderPizza("veg")
    println(pizza.prepare())
}