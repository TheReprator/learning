package org.example.creational

// 5. Prototype - Cloning Document
data class Document(var content: String) {
    fun clone(): Document = this.copy()
}

fun main() {
    // Prototype
    val doc1 = Document("Original NDA")
    val doc2 = doc1.clone()
    doc2.content = "Cloned NDA with edits"
    println(doc1)
    println(doc2)
}