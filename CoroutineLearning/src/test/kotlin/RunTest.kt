import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RunTest {

    @Test
    fun runTestAutoAdvance() = runTest {
        assertEquals(0, currentTime)
        delay(1000)
        assertEquals(1000, currentTime)
    }

/*
* When you use runTest { ... }, Kotlin sets up a test environment where:

    A TestCoroutineScheduler is created.
        * A StandardTestDispatcher is automatically used (unless you override it).
        * Time is virtual, but…
        * ✅ The test scheduler auto-advances time as needed — unless you disable it.
        * It starts a coroutine with TestScope and immediately advances it until idle.
        * The runTest function creates a scope; like all such functions, it awaits the completion of its children, which
            means that if you start a process that never ends, your test will never stop.


 runTestAutoAdvance(line 14) works because runTest automatically advances the virtual time until all tasks are completed —
    so delay(line 16) gets resumed, and the test completes.
* */

    @Test
    fun runTest_manualTime() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val scope = CoroutineScope(dispatcher)

        var result = "Not yet"

        scope.launch {
            delay(1000)
            result = "Done" // ❌ will NOT print without advancing time
        }

        // At this point, delay(1000) is scheduled
        assert(result == "Not yet")

        testScheduler.advanceTimeBy(1000) // advances virtual time
        testScheduler.runCurrent()        // 🔥 runs all tasks scheduled up to now (actually runs any tasks that became due.)

        // Now the coroutine has resumed
        assert(result == "Done")
    }

/*
You can disable the auto-time advancing behavior:

🧠 Summary of important pieces
    ✅ runTest provides testScheduler and a default TestScope.
    ✅ You can create your own StandardTestDispatcher(testScheduler) to manually control timing.
    ✅ To stop auto-advancing, just use your own dispatcher(line 36), and don’t call advanceTimeBy() or
        any other method directly, use the default testScheduler(like we had used 36,49,50) to move the virtual time


🔁 Key differences
Feature	                    runBlocking + StandardTestDispatcher	    runTest
Handles virtual time?	    ❌ Manual only	                            ✅ Auto by default
Scheduler?	                You manage it	                            Built-in
Safe for coroutine tests?	❌ Risk of hanging	                        ✅ Preferred
Runs in real time?	        🟡 Depends	                                ❌ No (only virtual time)
* */

    @Test
    fun test2() = runTest {
        assertEquals(0, currentTime)
        coroutineScope {
            launch { delay(1000) }
            launch { delay(1500) }
            launch { delay(2000) }
        }
        assertEquals(2000, currentTime)
    }
}