package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler


private fun standardTestDispatcher_type1() {
    val scheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(scheduler)
    CoroutineScope(testDispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }
    println("[${scheduler.currentTime}] Before")
    scheduler.runCurrent()
    println("[${scheduler.currentTime}] After")
}

/*
When we call delay, our coroutine is suspended and resumed after a set time. This behavior can be altered thanks to
TestCoroutineScheduler from kotlinx-coroutines-test, which makes delay operate in virtual time, which is fully simulated
and does not depend on real time.

StandardTestDispatcher is a scheduler-based virtual time dispatcher. It does not execute anything unless the scheduler
is advanced.

To use TestCoroutineScheduler on coroutines, we should use a dispatcher that supports it. The standard option is
StandardTestDispatcher. Unlike most dispatchers, it is not used just to decide on which thread a coroutine should run,
but it also advances virtual time. The most typical way to do this is by using advanceUntilIdle, other than advanceTimeBy(n),runCurrent()
which advances virtual time and invokes all the operations that would be called during that time if this were real time.

Here, we had used advanceUntilIdle(line 21), which causes all the enqueued task to run in specified order for the coroutine
that had used the StandardTestDispatcher as their dispatcher.

Since, CoroutineScope(line 13) had used StandardTestDispatcher as their dispatcher, so to run all the task, we had used
advanceUntilIdle, if we comment advanceUntilIdle(line 21), we will not be able to run the coroutine, nothing from, line 14-18,
will be printed
* */

private fun standardTestDispatcher_type2() {
    val dispatcher = StandardTestDispatcher()
    CoroutineScope(dispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }
    println("[${dispatcher.scheduler.currentTime}] Before")
    dispatcher.scheduler.advanceUntilIdle()
    println("[${dispatcher.scheduler.currentTime}] After")
}

/*
* StandardTestDispatcher creates TestCoroutineScheduler by default, so we do not need to do so explicitly. We can
* access it with the scheduler property.
*
* So, no need to create TestCoroutineScheduler that we had created in "standardTestDispatcher_type1" at line 11 and
* then pass the scheduler to dispatcher(line 13).
*
* Here, we had directly used the scheduler from dispatcher itself(line 58)
* */

private fun standardTestDispatcher_type3_runForever() {
    val testDispatcher = StandardTestDispatcher()
    runBlocking(testDispatcher) {
        //testDispatcher.scheduler.advanceTimeBy(1)
        delay(1)
        //testDispatcher.scheduler.advanceTimeBy(1)
        println("Coroutine done")
    }
    println("Coroutine outside")
}

/*
* It is important to notice that StandardTestDispatcher does not advance time by itself. We need to do this,
 otherwise our coroutine will never be resumed.
 *
 * 🔥 The problem: It never ends, the runBlocking call hangs forever. 💡StandardTestDispatcher does NOT advance
  virtual time by itself.

    Explanation:
        runBlocking(testDispatcher) {
            delay(1)
            println("Coroutine done")
        }

    Here, "Run a blocking coroutine on a test dispatcher that uses virtual time — but don’t ever tell it to advance time."
    So the coroutine suspends at delay(1), and just sits there forever, waiting for time to advance.

    Since, runBlocking is used for the program(line 74), which used StandardTestDispatcher as it's dispatcher(line 74),
    so, it remains blocked as StandardTestDispatcher never move by it's own

    even if we had uncommented the line 75, 77, it's going to make no difference, as the thread is already suspended,
    line 76. Due to delay(line 76), line 75 is of no use, as there is no suspension point before that.
    Once the thread enters the suspension at line 76(due to delay), there is no waking up, even after uncomment of line 77
    As the coroutine will never reach the line 77, as it is suspended, and runblocking is using StandardTestDispatcher,
    it will remain so(running foerver) until someone unblock from outside, which is not possible in current scenario,
    as the coroutine is runblocking

    💥 Think of it like this:
        You're in a car.
        The car won’t move unless you push it.
        But you're sitting inside the car trying to push it from the driver’s seat 😅 — it doesn't work.
* */

private fun standardTestDispatcher_type3_runForever_fix() {
    val testDispatcher = StandardTestDispatcher()

    runBlocking(Dispatchers.Default) { // ✅ use a real dispatcher to drive the test dispatcher
        launch(testDispatcher) {
            delay(1)
            println("Coroutine done")
        }

        // ✅ Let the delay be scheduled first
        testDispatcher.scheduler.advanceUntilIdle()

        println("Coroutine outside")
    }
}

/*
* So to fix problem(standardTestDispatcher_type3_runForever_fix), we had used the following approach,
*    runBlocking(Dispatchers.Default) + manually drive StandardTestDispatcher
* */


private fun standardTestDispatcher_type4() {
    val testDispatcher = StandardTestDispatcher()
    CoroutineScope(testDispatcher).launch {
        delay(1)
        println("Done1")
    }
    CoroutineScope(testDispatcher).launch {
        delay(2)
        println("Done2")
    }
    CoroutineScope(testDispatcher).launch {
        delay(3)
        println("Done3")
    }
    testDispatcher.scheduler.advanceTimeBy(3) // Done
    //testDispatcher.scheduler.runCurrent()
}

/*
* Here, we had used advanceTimeBy(line 151), so whatever coroutine can complete itself within 3 seconds will be called
* but not after that. That's why coroutine(line 139,143) will finish but not coroutine(line 147), as it needed more than 3 seconds
* but yes till that time, coroutine(line 147), is already out of suspension due to 3s(line 148).
*
* So if we uncomment line 152, coroutine(line 147) will also finish
* */

fun main() {
    println("standardTestDispatcher_type1() result")
    println()
    standardTestDispatcher_type1()

    println()
    println()
    println("standardTestDispatcher_type2() result")
    standardTestDispatcher_type2()

    /*println()
    println()
    println("standardTestDispatcher_type3_runForever() result")
    standardTestDispatcher_type3_runForever()*/

    println()
    println()
    println("standardTestDispatcher_type3_runForever_fix() result")
    standardTestDispatcher_type3_runForever_fix()

    println()
    println()
    println("standardTestDispatcher_type4() result")
    standardTestDispatcher_type4()
}

//https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/README.md