package org.example

import kotlinx.coroutines.*

private suspend fun type1(): Unit = coroutineScope {
    launch(Job()) {
            delay(1000)
        throw Exception("type1: job child Exception will not be called as Job is independent from parent or no waiting" +
                "for child coroutine from pareent")
    }
}

private suspend fun type2(): Unit = coroutineScope {
    launch(SupervisorJob()) {
        delay(1000)
        throw Exception("type2: SupervisorJob child Exception will not be called as Job is independent from parent or no waiting" +
                "for child coroutine from pareent")
    }
}

/*
* Here type1 and type2 is same, as both are using a job in their respective builders(line 6, line 14),
* which breaks the parent child relationship like we discussed in example Job_4_ParentChildRelationShip,
* Job_5_Independent.
* So using a SupervisorJob, will not make any sense
* */

private suspend fun type3(): Unit = coroutineScope {
    launch(SupervisorJob()) { //launch(Job())
        delay(1000)
        throw Exception("type3: SupervisorJob child Exception will be called as other child builder is running on " +
                "parent context, which gives time to this independent job to execute itself")
    }

    launch {
        delay(2000)
        println("type3: parent coroutine builder")
    }
}

/*
* Here type3, builder(line 29) is running with SupervisorJob as parent, this builder will not have finished it's job,
* if parent builder(line 35) delay is lesss than that of SupervisorJob job builder(line 30).
*
* Considering the current scenario, the exception thrown(line 31) will be not be propagated to parent(line 28),
* as the builder(line 29) is using or create a Job of its own, which breaks the parent child relationship.
*
* So, does not matter, whether you use launch(Job()) or launch(SupervisorJob()) result will be always same
* */

private suspend fun type4(): Unit = coroutineScope {
    val job = SupervisorJob()
    launch(job) {
        delay(1000)
        throw Exception("type4: SupervisorJob child Exception will be called as other child builder is running on parent context," +
                "which gives time to this independent job to execute itself")
    }

    launch(job) {
        delay(500)
        println("type4: parent coroutine builder")
    }

    launch(job) {
        delay(1500)
        println("type4: parent coroutine builder 1500")
    }

    job.children.forEach {
        it.join()
    }
}

/*
* So, if we want to create a SupervisorJob, which can handle the exception for all this child builders, then
* create a job like(line 52) and pass the job as parent to all the builders, like(line 53, 59, 64).
*
* So, when an exception(line 55) is thrown in coroutine builder(line 53), it will not affect the other coroutine
* builder of that job(line 59(already finished), line 64)
* */
fun main(): Unit = runBlocking {
    type1()
    println()
    println()
    type2()
    println()
    println()
    type3()
    println()
    println()
    type4()
}