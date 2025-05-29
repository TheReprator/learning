package org.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
Terminal lifecycle operators in Kotlin Flow like:
onStart
onCompletion
catch

They are called terminal lifecycle operators because:
    They interact with the lifecycle of the flow execution, i.e. when it starts, completes, or fails.
    But they only execute when a terminal operator is called — such as collect(), first(), toList(), launchIn etc.
        as it does not wait for the first or any element

    In other words:
        🛑 Without a terminal operator, your flow doesn't run at all — and so, neither do onStart, onCompletion, or catch.

Terminal lifecycle operators:
    These operators can also emit elements. Such elements will flow downstream from this place.
* */

/*
    Runs before the flow starts emitting values.
* */
private fun flow_2_terminalLifeCycle_OnStart() = runBlocking {
    flowOf<Int>(1,2,3)
        .onStart {
            println("Before")
            emit(10)
        }
        .collect { println(it) }
}

/*
    Runs after the flow completes — successfully or with error.
* */
private fun flow_2_terminalLifeCycle_OnCompletion() = runBlocking {
    flowOf<Int>(1,2,3)
        .onCompletion {
            println("Completed")
            emit(10)
        }
        .collect { println(it) }
}

private fun flow_2_terminalLifeCycle_OnCompletion_exception() = runBlocking {
    val job = launch {
        flowOf(1, 2)
            .onEach { delay(1000) }
            .onCompletion { println("Completed") }
            .collect { println(it) }
    }
    delay(1100)
    job.cancel()
}

/*
    Catches exceptions that happen upstream in the flow.
    🧠 Note: catch only catches exceptions from upstream flows. If you throw from collect, it won’t be caught by catch.
* */
private fun flow_2_terminalLifeCycle_OnCatch() = runBlocking {
    flow {
        emit(1)
        emit(2)
        throw RuntimeException("Failed!")
        emit(3)
    }.onEach {
        println("Catch onEach $it")
        //throw RuntimeException("onEach Exception Failed!")
    }.catch { e ->
        println("Catch Caught ${e.message}")
        emit(-1) //fallback
    }.onEach {
        println("Catch onEach Second $it")
        //throw RuntimeException("onEach Exception Failed!")
    }.collect { println(it) }
}
/*
Output:
    Catch onEach 1
    Catch onEach Second 1
    1
    Catch onEach 2
    Catch onEach Second 2
    2
    Catch Caught Failed!
    Catch onEach Second -1
    -1

Observation:
    1)The catch method stops an exception by catching it(line 79). The previous steps have already been completed, but catch
        can still emit new values(line 81) and keep the rest of the flow alive. But can't emit more elements other than
        from catch operator
    2)Notice, oneach is not called, when error is thrown
    3)If we uncomment line 78, inside onEach, then still it will be caught inside catch, as catch is after the onEach(76),
        But, if we had uncommented exception at line 84 inside onEach, then "catch" operator will not be able to catch
        the exception, as this each block is after catch block, so this proves that, "catch" operator only catches
        exceptions from upstream flows.
* */

private fun flow_2_terminalLifeCycle_OnCatch_exception() = runBlocking {
    try {
        flow {
            emit(1)
            emit(2)
        }.catch { e ->
            println("Catch Caught ${e.message}")
        }.collect {
            println(it)
            throw RuntimeException("collect Failed!")
        }
    } catch (e: Exception) {
        println("Catch Exception ${e.message}")
    }
}

/*
* Flow with try catch-->
    Output:
        1
        2
        Catch Exception collect Failed!

    Observation:
        Catch can not protect you from exception occurring inside collect(line 119). In android it will crash the
        application as every exception inside coroutine is fatal exception for android while for other i.e. desktop,
        it is normal std error
* */
fun main(): Unit = runBlocking {
    println("<-----Terminal lifecycle operators-->")
    println("OnStart-->")
    flow_2_terminalLifeCycle_OnStart()
    println()
    println()
    println("OnCompletion-->")
    flow_2_terminalLifeCycle_OnCompletion()
    println()
    println()
    println("OnCompletion Exception-->")
    flow_2_terminalLifeCycle_OnCompletion_exception()
    println()
    println()
    println("OnCatch-->")
    flow_2_terminalLifeCycle_OnCatch()
    println()
    println()
    println("Flow with try catch-->")
    flow_2_terminalLifeCycle_OnCatch_exception()
}