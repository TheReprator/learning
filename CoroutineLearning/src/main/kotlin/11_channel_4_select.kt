package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select

/*
select is a powerful coroutine primitive, that allows you to await multiple suspending operations simultaneously and
act on the first one that becomes available.
    When working with channels, select is useful to receive from multiple channels concurrently

    It works with:
        Channels (onReceive, onSend)
        Deferred (promises/futures via onAwait)
        Timeouts
        Custom suspending functions
* */

private suspend fun requestData1(): String {
    delay(100_000)
    return "Data1"
}

private suspend fun requestData2(): String {
    delay(1000)
    return "Data2"
}

private val scope = CoroutineScope(SupervisorJob())

private suspend fun askMultipleForData(): String {
    val defData1 = scope.async { requestData1() }
    val defData2 = scope.async { requestData2() }
    return select {
        defData1.onAwait { it }
        defData2.onAwait { it }
    }
}

/*
Notice that async(line 32,33) is started in an outside scope. This means that if you cancel the coroutine that
started askMultipleForData(line 31), these async tasks will not be cancelled.
    This is a problem, but I don’t know any better implementation. If we had used coroutineScope, then it would have
awaited its children;

So, for the implementation above, we’ll receive Data2 as a result. Here select(line 34), is operating on
2 async(line 35,36), whoever first produces the result will be dispatched from the expression(line 34) and select will
exit and therefore the method, askMultipleForData(line 31)
* */

private suspend fun CoroutineScope.selectChannelProduceString(
    s: String,
    time: Long
) = produce {
    while (true) {
        delay(time)
        send(s)
    }
}

private fun selectChannel() = runBlocking {
    val fooChannel = selectChannelProduceString("foo", 210L)
    val barChannel = selectChannelProduceString("BAR", 500L)
    repeat(7) {
        select {
            fooChannel.onReceive {
                println("From fooChannel: $it")
            }
            barChannel.onReceive {
                println("From barChannel: $it")
            }
        }
    }
    coroutineContext.cancelChildren()
}

/*
The select function can also be used with channels. Here we are creating 2 channels(line 62,63) and creating a loop for 7
time, in loop body, we use a select expression in "onReceive"(line 66, 69) of channel and returns the immediate result of
first receive,

Output is as follows,
    From fooChannel: foo
    From fooChannel: foo
    From barChannel: BAR
    From fooChannel: foo
    From fooChannel: foo
    From barChannel: BAR
    From fooChannel: foo

Explanation:
    Here, fooChannel(line 62) is evaluated twice as delay is 210 ms which is almost double barChannel(500ms), that's wny
first 2 belongs to fooChannel, after that barChannel is printed
* */


private fun CoroutineScope.selectChannel1()  {
    val c1 = Channel<Char>(capacity = 2)
    val c2 = Channel<Char>(capacity = 2)
// Send values
    launch {
        for (c in 'A'..'C') {
            delay(400)
            select<Unit> {
                c1.onSend(c) { println("Sent $c to 1") }
                c2.onSend(c) { println("Sent $c to 2") }
            }
        }
    }
// Receive values
    launch {
        while (true) {
            delay(1000)
            val c = select<String> {
                c1.onReceive { "$it from 1" }
                c2.onReceive { "$it from 2" }
            }
            println("Received $c")
        }
    }
}

/*
* selectChannel1 is a never ending program, as coroutine(112) is always active due to active while loop(line 113).
* Here we are using select both on onReceive(116, 117) and onSend(106,107).
*   The select will behave the same way as earlier, whoever provides the result first will be the result of select
* expression
* */
fun main() = runBlocking {
    //askMultipleForData()
    //selectChannel()
    selectChannel1()
}