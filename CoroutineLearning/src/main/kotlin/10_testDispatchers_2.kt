package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlin.random.Random
import kotlin.system.measureTimeMillis


private fun standardTestDispatcher2_type1() {
    val dispatcher = StandardTestDispatcher()
    CoroutineScope(dispatcher).launch {
        delay(1000)
        println("Coroutine done")
    }

    Thread.sleep(Random.nextLong(2000)) // Does not matter
                        // how much time we wait here, it will not influence the result

    val time = measureTimeMillis {
        println("[${dispatcher.scheduler.currentTime}] Before")
        dispatcher.scheduler.advanceUntilIdle()
        println("[${dispatcher.scheduler.currentTime}] After")
    }
    println("Took $time ms")
}

/*
* This example shows that virtual time is truly independent of real time. Adding Thread.sleep(line 21) will not influence
the coroutine with StandardTestDispatcher. Call to advanceUntilIdle(line 26) takes only a few milliseconds, so it does not
wait for any real time of Adding Thread.sleep(line 21). StandardTestDispatcher(line 15) immediately pushes the virtual time
and executes coroutine operations
*
* The whole program had taken around 16ms, ignoring Thread.sleep(line 21) of 2 secons.
* */


private fun testScope() {
    val scope = TestScope()
    scope.launch {
        delay(1000)
        println("First done")
        delay(1000)
        println("Coroutine done")
    }
    println("[${scope.currentTime}] Before") // [0] Before
    scope.advanceTimeBy(1000)
    scope.runCurrent() // First done
    println("[${scope.currentTime}] Middle") // [1000] Middle
    scope.advanceUntilIdle() // Coroutine done
    println("[${scope.currentTime}] After") // [2000] After
}

/*
* Till previous examples, we were using StandardTestDispatcher and wrapping it with a scope. Instead, we could use TestScope,
which does the same (and it collects all exceptions with CoroutineExceptionHandler). The trick is that on this scope we
can also use functions like advanceUntilIdle, advanceTimeBy, or the currentTime property , all of which are delegated to the
scheduler used by this scope
* */

/*
runTest
Please see example in (test/kotlin/RunTest.kt)

backgroundScope
Please see example in (test/kotlin/BackgroundScopeTest.kt)

runTest is the most commonly used function from kotlinx-coroutines-test. It starts a coroutine with TestScope
and immediately advances it until idle. Within its coroutine, the scope is of type TestScope, so we can check
currentTime at any point. Therefore, we can check how time flows in our coroutines, while
our tests take milliseconds.

The runTest function creates a scope; like all such functions, it awaits the completion of its children.
This means that if you start a process that never ends, your test will never stop.

please see
image/10_testScope_hierarchy.png for the hierarchy
*/

fun main() {
    println("standardTestDispatcher2_type1() result")
    standardTestDispatcher2_type1()

    println()
    println()
    println("testScope() result")
    testScope()
}