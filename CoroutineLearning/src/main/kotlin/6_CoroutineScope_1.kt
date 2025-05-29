package org.example

import kotlinx.coroutines.*
import java.lang.Exception

/*
* coroutineScope is a suspending function that starts a scope. It re-turns the value produced by the argument function.
*
* It formally creates a new coroutine, but it suspends the previous one until the new one is finished,
* so it does not start any concurrent process.
* */

private fun type1() = runBlocking {

    val a = coroutineScope {
        println("type1: inside a")
        delay(3000)
        10
    }
    println("a is calculated")
    val b = coroutineScope {
        println("type1: inside b")
        delay(5000)
        20
    }
    println(a)
    println(b)
}

/*
* Here, when a new coroutine is created with coroutineScope(line 15, line 19). It suspends it's immediate parent
* runblocking(line 13,18), while coroutineScope is being executed(line 15, line 19), this what statement at line 8-10 want's to convey
* */

/*
* The provided scope inherits its coroutineContext from the outer scope, but it overrides the context’s Job.
* Thus, the produced scope respects its parental responsibilities:
        • inherits a context from its parent;
        • waits for all its children before it can finish itself;
        • cancels all its children when the parent is cancelled.
        • Propagates exceptions, meaning if a child coroutine fails, the entire coroutineScope fails.
* */

private suspend fun longTask() = coroutineScope {
    launch {
        delay(2000)
        val name = coroutineContext[CoroutineName]?.name
        println("type2: [$name] Finished task 1")
    }
    launch {
        delay(4000)
        val name = coroutineContext[CoroutineName]?.name
        println("type2: [$name] Finished task 2")
    }

    launch {
        delay(1500)
        val name = coroutineContext[CoroutineName]?.name
        println("type2: [$name] Finished task 3")
    }
}

private fun type2() = runBlocking(CoroutineName("Parent coroutineScope")) {
    println("type2: Before")
    longTask()
    println("type2: After")
}
/*
* We can see that “After” will be printed at the end because coroutineScope(line 41) will not finish until all its
* children (line 45, 50, 56) are finished. Also, CoroutineName(line 63) is properly passed from parent to child, which
* clarifies the statement from line 38-41,
*           Whereas with custom job, supervisorJob, like in example below,
*       2_Job_4_ParentChildRelationShip.kt
*       3_JobCancel_1.kt
*       2_Job_5_Independent.kt
*   either we are using join on parent to wait for child to finish, or we are delaying with sufficient time
* from parent, so that child can finish their task.
*       This is what gives the coroutineScope upper hand, as it waits for all its child by default to finish their task
* before finishing itself
* */


/*
* A cancelled parent leads to the cancellation of unfinished children.
* If there is an exception in coroutineScope or any of its children, it cancels all its unfinished children and
* rethrows it.
* */
private suspend fun longTaskCancellation() = coroutineScope {
    launch {
        delay(1000)
        val name = coroutineContext[CoroutineName]?.name
        println("type3: [$name] Finished task 1")
        throw Exception("tezt")
    }
    launch {
        delay(2000)
        val name = coroutineContext[CoroutineName]?.name
        println("type3: [$name] Finished task 2")
    }
    supervisorScope {
        delay(4000)
        val name = coroutineContext[CoroutineName]?.name
        println("type3: [$name] Finished task 3")
    }
}

private fun type3()= runBlocking {
    val job = launch(CoroutineName("Parent coroutineScope")) {
        longTaskCancellation()
    }
    delay(1500)
    job.cancel()
}

/*
* Here, at line 108, we get a reference of job from a coroutine, which include coroutineScope(line 88), as their child.
* So, whenever a parent job is cancelled(line 112) or an exception is thrown inside coroutineScope,
* coroutineScope(line 88) will cancel itself and cancel it's unfinished children(line 95,100), irrespective of scope like,
* supervisorScope(line 100), which is also cancelled, due to being child of coroutineScope, which proves our
* statement line 41, line 84-86
* */

fun main() = runBlocking {
    //type1()
    //type2()
    type3()
}