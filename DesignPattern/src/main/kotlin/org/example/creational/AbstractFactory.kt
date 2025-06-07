package org.example.creational

interface Chair {
    fun type(): String
}

class VictorianChair : Chair {
    override fun type() = "Victorian Chair"
}

class ModernChair : Chair {
    override fun type() = "Modern Chair"
}

interface Sofa {
    fun type(): String
}

class VictorianSofa : Sofa {
    override fun type() = "Victorian Sofa"
}

class ModernSofa : Sofa {
    override fun type() = "Modern Sofa"
}

//It is Abstract factory pattern
interface FurnitureAbstractFactory {
    fun createChair(): Chair
    fun createSofa(): Sofa
}

class VictorianFurnitureFactory : FurnitureAbstractFactory {
    override fun createChair() = VictorianChair()
    override fun createSofa() = VictorianSofa()
}

class ModernFurnitureFactory : FurnitureAbstractFactory {
    override fun createChair() = ModernChair()
    override fun createSofa() = ModernSofa()
}


fun main() {

    // Create modern furniture using the ModernFurnitureFactory
    val modernFactory: FurnitureAbstractFactory = ModernFurnitureFactory()
    val modernChair: Chair = modernFactory.createChair()
    val modernSofa: Sofa = modernFactory.createSofa()


    // Use the modern furniture
    println(modernChair.type())
    println(modernSofa.type())

    println()
    println()

    // Create Victorian furniture using the VictorianFurnitureFactory
    val victorianFactory: FurnitureAbstractFactory = VictorianFurnitureFactory()
    val victorianChair: Chair = victorianFactory.createChair()
    val victorianSofa: Sofa = victorianFactory.createSofa()


    // Use the Victorian furniture
    println(victorianChair.type())
    println(victorianSofa.type())
}