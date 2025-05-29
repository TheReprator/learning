package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlin.random.Random
import kotlin.system.measureTimeMillis


fun main() {
    CoroutineScope(StandardTestDispatcher()).launch {
        print("A")
        delay(1)
        print("B")
    }
    CoroutineScope(UnconfinedTestDispatcher()).launch {
        print("C")
        delay(1)
        print("D")
    }
}
/*
⚙️ What is UnconfinedTestDispatcher?
    UnconfinedTestDispatcher is a special test dispatcher that executes coroutines immediately and eagerly, without
    confining them to a thread or queue. It’s meant for testing, and it's similar in spirit to Dispatchers.Unconfined
    but safer and more predictable for tests.

🔍 When to use UnconfinedTestDispatcher
    ✅ 1. You want everything to run now, no delay control

🧠 Characteristics
    Feature	Behavior
        Runs tasks immediately?	        ✅ Yes
        Virtual time?	                ❌ No (not by default)
        Useful for?	                    Unit testing simple logic without delay/time jumping
        Good with runTest?	            ✅ Yes, especially when you want to avoid manual advancing
        Needs advanceTimeBy()?	        ❌ Not unless mixed with other test dispatchers


🆚 runTest vs UnconfinedTestDispatcher
    Feature	                                runTest (default)	            runTest + UnconfinedTestDispatcher
    Time control	                        ✅ Yes (virtual time)	        ✅ Yes (via testScheduler)
    Runs immediately?	                    🔄 Depends on dispatcher	    ✅ Always immediately
    Queued tasks	                        ✅ You must advanceTimeBy()	    ❌ Executes right away
    Use for delay-heavy tests	            ✅ Best choice	                🟡 Only with manual time control
    Use for lifecycle/dispatcher testing	🔄 Needs dispatcher override	✅ Natural & predictable
    Flow tests	                            ✅ Excellent	                🟡 Use carefully


The biggest difference between StandardTestDispatcher and UnconfinedTestDispatcher, is that standardTestDispatcher does
not invoke any operations until we use its scheduler. UnconfinedTestDispatcher immediately invokes
all the operations before the first delay on started coroutines, which is why the code below prints “C”.
* */
