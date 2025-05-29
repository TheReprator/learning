package org.example

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    val job1 = Job()
    launch(job1) {
        repeat(5) {
            delay(200)
            println("Rep: $it")
        }
    }

    launch {
        delay(500)
        println("complete job will be called")
        job1.completeExceptionally(Exception("complete asap"))
        println("complete job is already called")
    }

    job1.join()
    launch(job1) {
        println("will not execute, as job is already completed!")
    }
}

/*
* Once the job is complete with exception(line 20), it cancel all its subchild(line 10) instantly without waiting
* for the child to complete and can't be used further(line 25)
* */