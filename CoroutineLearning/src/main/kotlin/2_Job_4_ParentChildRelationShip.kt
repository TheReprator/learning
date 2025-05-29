package org.example

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val jobIndependent = Job()
    val job = launch(jobIndependent) {
        delay(1000L)
        println("will not be printed, job")
    }

    val job2 = launch(Job()) {
        delay(1000L)
        println("will not be printed, job2")
    }

    println(job)
    println(job2)
}

/*
* Since job is the only element that is not inhertited by child coroutine, So once you are creating you own
* child job(line no. 9, 10,15), then you are independent from parent. No more structruced concurrnecy will be there
* as we broke the parent child relatinhsip. Now, child job has to be taken care by itself
*
* So, here, job, job2 are running independently, as they have no relatinship with parent runblocking(due to job override),
* that's why parent runblocking is not waiting for them to finish
* */