package org.example

import kotlinx.coroutines.*

fun main() = runBlocking {
    val coroutineName = CoroutineName("vikramJob")
    val parentJob = coroutineContext.job

    val childJobParent = launch(coroutineName) {
        val childName = coroutineContext[CoroutineName]
        println("inside Child, Are name equals ${childName == coroutineName}")
        delay(1000L)
        val childJob = coroutineContext[Job]
        println("inside Child, Are job equal ${childJob == parentJob}")
        println("inside Child, Are job equals ${childJob == parentJob.children.first()}")
    }

    println("Are job equal ${childJobParent == parentJob}")
    println("Are job equals ${childJobParent == parentJob.children.first()}")
}