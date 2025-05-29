import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BackgroundScopeTest {

    @Test
    fun `runTest issue`() = runTest {
        var i = 0
        launch {
            while (true) {
                delay(1000)
                i++
            }
        }
        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2, i)
        // Test would pass if we added
        // coroutineContext.job.cancelChildren()
    }

    /*
    *
    * As we know from runTestAutoAdvance() in file RunTest, The runTest function creates a scope; like all such functions, it
    awaits the completion of its children.

    So, here coroutine launch(line 17) is continue to run, hence runTest(line 15) waits for its body to finish with default
    timeout of 1 minute, if it complete it body withing 1 minute, it will pass but since coroutine launch(line 17) is continue
    to run, it fail

    But if we uncomment the line(28), it will pass as all the coroutine child will be finished, So to counter this
    backgroundScope is used in next test function(runTest issue fix with backgroundScope)
    * */

    @Test
    fun `runTest issue fix with backgroundScope`() = runTest {
        var i = 0

        // Launch in backgroundScope so it survives beyond runTest's main body
        backgroundScope.launch {
            while (true) {
                delay(1000)
                i++
            }
        }
        delay(1001)
        assertEquals(1, i)
        delay(1000)
        assertEquals(2, i)
    }

    /*
    🔹 Purpose:
        Launch coroutines that don’t get cancelled when the runTest block exits.
        Useful for testing ongoing tasks, like Flow, long-lived workers, etc.

     🔍 What happens here:
        backgroundScope.launch { delay(1000) ... } is started independently.
        It's not tied to the lifecycle of the test block itself.


    ✅ When to use backgroundScope
        Use Case	                                        Should You Use backgroundScope?
        Long-running background tasks	                    ✅ Yes
        Simulating ViewModel's internal coroutines	        ✅ Yes
        You need tight control over timing and lifecycle	✅ Yes
        Simple unit tests with auto-advancing	            ❌ Not necessary
    * */
}