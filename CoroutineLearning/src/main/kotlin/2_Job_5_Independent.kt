package org.example

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    //neverEndingJob()
    waitingForAllChildToComplete()
}

/*
It will run forever, even after applying join, as job instance is used by launch builder, which is still running.

So, to complete the program, we have to wait for completion of all child, created via that job like in example,
waitingForAllChildToComplete
* */
private suspend fun CoroutineScope.neverEndingJob() {
    val jobIndependent = Job()
    launch(jobIndependent) {
        delay(1000L)

        println("child job running, job")
    }

    launch(jobIndependent) {
        delay(2000L)
        println("child job running, job 2")
    }

    println("job waitaing child1")
    jobIndependent.join()
    println("final call")
}

private suspend fun CoroutineScope.waitingForAllChildToComplete() {
    val jobIndependent = Job()
    launch(jobIndependent) {
        delay(1000L)
        println("child job running, job")
    }

    launch(jobIndependent) {
        delay(2000L)
        println("child job running, job 2")
    }

    println("job waitaing child1")
    jobIndependent.children.forEach {
        it.join()
    }
    println("final call")
}