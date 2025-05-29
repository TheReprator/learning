package org.example

import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    val job = launch {
            delay(1000)
            println("Job launched")
        }

    job.invokeOnCompletion { cause ->
        println("Job completed or canceled")
        println("Reason: $cause")

        launch(job) {
            delay(1000)
            println("Job launched from invokeOnCompletion with same job, so will not be printed")
        }

        launch {
            delay(2000)
            println("Job launched from invokeOnCompletion")
        }
    }

    //job.cancelAndJoin()
    println("done")
}

/*
* Job invokeOnCompletion(line 11) is called synchronously during cancellation, so we did not control the thread
* in which it is running
*
* Here, builder(line 15) will not run, as the job is already completed. Builder(line 20) is independent, as we are
* not sure, whether it will run or not, as during cancellation, we did not control the thread in which it is running
*
* */