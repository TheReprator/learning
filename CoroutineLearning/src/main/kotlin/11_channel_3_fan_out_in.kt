package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private fun CoroutineScope.fanOutSenderProduceNumbers() = produce {
    repeat(10) {
        delay(1000)
        println("#$it sent")
        send(it)
    }
}

private fun CoroutineScope.fanOutlaunchProcessor(
    id: Int,
    channel: ReceiveChannel<Int>
) = launch {
    for (msg in channel) {
        println("coroutine #$id received $msg")
    }
}

private suspend fun fanOut(): Unit= coroutineScope {
    val channel = fanOutSenderProduceNumbers()
    repeat(3) { id ->
        delay(10)
        fanOutlaunchProcessor(id, channel)
    }
}

/*
Here, channel(line 10) is of default(Rendezvous), which does not keep anything, once it is sent from producer, it is
delivered or ejected, whether there is any receiver or not, does not matter

At line 10, a chanel is created of type Rendezvous, which is producing or sending the events, At line 28, we are creating
an instance of that sender, then we are creating channel receivers(line 18), with loop(line 29)

output:
    #0 sent
    coroutine #0 received 0
    #1 sent
    coroutine #1 received 1
    #2 sent
    coroutine #2 received 2
    #3 sent
    coroutine #0 received 3
    #4 sent
    coroutine #1 received 4
    #5 sent
    coroutine #2 received 5
    #6 sent
    coroutine #0 received 6
    #7 sent
    coroutine #1 received 7
    #8 sent
    coroutine #2 received 8
    #9 sent
    coroutine #0 received 9

The elements are distributed fairly. The channel has a FIFO (first-in-first-out) queue of coroutines waiting for an
element. This is why in the above example you can see that the elements are received by the
next coroutines (0, 1, 2, 0, 1, 2, etc).

Metaphor:
    To better understand why, imagine kids in a kindergarten queuing for candies. Once they get some, they immediately
eat them and go to the last position in the queue. Such distribution is fair (assuming the number of candies is a
multiple of the number of kids, and assuming their parents are fine with their children eating candies).

This is called fan-out, where we have 1 sender and multiple receiver
* */

suspend fun fanInSendString(
    channel: SendChannel<String>,
    text: String,
    time: Long
) {
    while (true) {
        delay(time)
        channel.send(text)
    }
}

private fun fanIn() = runBlocking {
    val channel = Channel<String>()
    launch {
        fanInSendString(channel, "foo",200L)
    }
    launch {
        fanInSendString(channel, "BAR!", 500L)
    }
    repeat(15) {
        println(channel.receive())
    }
   coroutineContext.cancelChildren()
}

/*
Fan-in: Multiple coroutines can send to a single channel. In the below example, you can see two coroutines(line 93,97)
sending elements to the same channel.
* */

private fun CoroutineScope.pipeLineProducerNumbers(): ReceiveChannel<Int> =
    produce {
        repeat(3) { num ->
            send(num + 1)
        }
    }

private fun CoroutineScope.pipeLineDependentProducerSquare(numbers: ReceiveChannel<Int>) =
    produce {
        for (num in numbers) {
            send(num * num)
        }
    }

private suspend fun pipeline() = coroutineScope {
    val numbers = pipeLineProducerNumbers()
    val squared = pipeLineDependentProducerSquare(numbers)
    for (num in squared) {
        println(num)
    }
}

/*
Sometimes we set two channels such that one produces elements based on those received from another. In such cases,
we call it a pipeline.

Here, pipeLineDependentProducerSquare(line 117) is dependent on result or event of pipeLineProducerNumbers(line 110),
whenever pipeLineDependentProducerSquare(line 117), produce a result, we capture it or consume it will, for each(line 127)
* */
suspend fun main(): Unit = coroutineScope {
    //fanOut()
    //fanIn()
    pipeline()
}