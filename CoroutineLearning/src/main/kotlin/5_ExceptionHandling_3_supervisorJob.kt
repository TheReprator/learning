package org.example

import kotlinx.coroutines.*

private suspend fun type1(): Unit = coroutineScope {
    val scope = CoroutineScope(SupervisorJob())
    scope.launch {
            delay(1000)
            throw Exception("SupervisorScope child Exception")
    }

    scope.launch {
        delay(2000)
        println("SupervisorScope other child")
    }

    launch {
        delay(2000)
        println("parent coroutine builder")
    }
}

/*
* Supervisor job is a special kind of job, that ignore all exceptions in its children. It is generally used as
* part of scope
*
*
*  SupervisorJob only helps with job cancellation hierarchy, but does NOT catch or report exceptions.
    If you don’t handle exceptions, they still go uncaught, and:
    If the exception bubbles to the top of any thread, including IO thread,
    and there's no exception handler attached to the coroutine →➡ Crash!(in android, if not used coroutine exception handler)
    but will not crash in plain kotlin program(uncaught coroutine exceptions are printed to stderr, but do NOT crash the
    JVM process, if used with supervisor job)


    Behavior                        | Regular JVM app       | Android
    Uncaught exception in coroutine | Printed to stderr     | Treated as crash (like uncaught Thread exception)
    Main dispatcher                 | Custom event loop     | Android’s Looper + Handler
    OS behavior                     | Continues running     | Kills the app process immediately
*
* Here, when we throw an exception(line 9), it is propagated to parentScope(line 6), but due to SupervisorJob nature
* in scope(line 6), it just cancel child(line 7) which had thrown exception and does not cancel itself(line 6) and
* it's other children(line 12)
* */


fun main(): Unit = runBlocking {
    type1()
    println()
    println()
    type2()
}

private suspend fun type2(): Unit = coroutineScope {
    val scope = CoroutineScope(SupervisorJob())
    val asyncDefer = scope.async {
        delay(1000)
        throw Exception("SupervisorScope child Exception")
    }

    scope.launch {
        delay(500)
        println("SupervisorScope other child")
    }

    scope.launch {
        delay(2000)
        println("SupervisorScope other child1")
    }

    launch {
        delay(2000)
        println("parent coroutine builder")
    }

   asyncDefer.await()
}
/*
* Here, using async(line 56) with coroutine SupervisorJob(line 55), it still crash the application on using
* .await(line 76), does not respecting the SupervisorJob. It behave the same way without SupervisorJob like in
* example ExceptionHandling_2
*
* async requires you to call .await() to surface exceptions, when they are in supervisor job or supervisour scope,
* If we had commented ( asyncDefer.await() line 76), it will not crash
* */