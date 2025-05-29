package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
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

/*
📦 What is a Channel in Kotlin Coroutines?
    A Channel<T> is like a concurrent queue used to send and receive values between coroutines.

🔁 Analogy
    Imagine a public bookself or bookcase:
        One person(coroutine) needs to bring a book(data)
        for another person(another coroutine) to find(receive) book(data)
    No shared state. Just messages being passed.

Channel supports any number of senders and receivers, and every value that is sent to a channel is received only once.
    Image: images/11_channel_1.png

Channel is an interface that implements two other interfaces:
    • SendChannel, which is used to send elements (adding elements) and to close the channel;
    • ReceiveChannel, which receives (or takes)the elements.

🧠 Channel Key Concepts
    Concept	                    Meaning
    send(value)	                Send a value to the channel (suspends if channel is full)
                                    Example(valid only in case of full): Like in our metaphorical bookshelf, when someone
                                    goes to the shelf to find a book but the bookshelf is empty, that person needs to
                                    suspend until someone puts an element(book) there
    receive()	                Get a value from the channel (suspends if channel is empty)
                                    Example(valid only in case of empty): Like in our metaphorical bookshelf, when someone
                                    tries to put a book on a shelf but it is full, that person needs to suspend until
                                    someone takes a book and makes space.
    for (x in channel)	        Iterate over all values until the channel is closed
    close()	                    Close the channel — no more send() allowed
    trySend() / tryReceive()	Non-suspending, safe attempts to send/receive
* */

private suspend fun producerConsumer_type1(): Unit= coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
        }
    }
    launch {
        repeat(5) {
            val received = channel.receive()
            println("consuming $received")
        }
    }
}

/*
*
🧯 Common Mistakes
        Mistake	                                        Fix
        Not closing channel	                            Always call channel.close() when done sending
        Using receive() without checking closed state	Use for (x in channel) instead
        Blocking send() forever	                        Use buffer or unlimited if needed
* */


suspend fun producerConsumer_type2(): Unit= coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) { index ->
            println("Producing next one")
            delay(1000)
            channel.send(index * 2)
        }
        channel.close()
    }
    launch {
        for (element in channel) {
            println("consuming $element")
        }
        // or
        // channel.consumeEach { element ->
        // println("consuming $element")
        // }
    }
}

/*
Here, we overcome the issue of Example: producerConsumer_type1, where we lack the following points:
    The receiver does not know how many elements will be sent
    Using receive() without checking closed state

Solution to above 1st problem:
    To receive elements on the channel until it is closed, we could use a for-loop(line 91) or consumeEach function(line 95)
* */


private suspend fun producerConsumer_type3_produce(): Unit= coroutineScope {
    val channel = produce {
        repeat(5) { index ->
            println("Producing next one")
            delay(1000)
            send(index * 2)
        }
    }
    for (element in channel) {
        println("consuming $element")
    }
}

/*
The produce function in Kotlin coroutines creates a coroutine that returns a ReceiveChannel<T>, so it acts like a
producer of values that others can consume.

The produce function closes the channel whenever the builder coroutine ends in any way (finished, stopped, cancelled).
Thanks to this, we will never forget to call close. The produce builder is a very popular way to create a channel,
and for good reason: it offers a lot of safety and convenience.

Solution to above 2nd(line 105) problem:
    Due to auto close feature of produce function, we never have to worry about the channel close, Hence it sort the
    issue arised due to producerConsumer_type1(), producerConsumer_type2()

🔄 Under the hood
    This example:
        val ch = produce {
            send(1)
        }

    Is like doing:
        val ch = Channel<Int>()
        launch {
            ch.send(1)
            ch.close()
        }

    This way produce handles the Channel creation, closure and coroutine management for you.
* */


/*
💡 Different Types of Channels
    Channel Type	                            Behavior
    Channel<T>() or Channel.RENDEZVOUS	        Default, rendezvous channel (0 buffer)
                                                    Example: It means that an exchange can happen only if sender and
                                                    receiver meet (so it is like a book exchange spot, instead of a bookshelf).
    Channel<T>(capacity)	                    Buffered
    Channel.UNLIMITED	                        Never suspends on send()
    Channel.CONFLATED	                        Always keeps latest value only (each new element replaces the previous one)
    Channel.BUFFERED	                        System default size buffer(which is 64 by default)
* */
 fun main(): Unit = runBlocking {
     //producerConsumer_type1()
     //producerConsumer_type2()
     producerConsumer_type3_produce()
}