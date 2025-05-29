package org.example

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
    Different Types of Channels
        Channel Type            | Behavior                      | Suspends on send()?                | Keeps...                      | Good for
        Rendezvous (default)    | No buffer                     | ✅ Yes (until receive() happens)   | Nothing                       | Tight sync between sender/receiver
        Buffered                | Fixed-size buffer             | ✅ When full                       | Buffer content                | Slight decoupling
        Unlimited               | Infinite buffer               | ❌ Never                           | All sent items                | Fast-fire, lossless queue
        Conflated               | One slot, overwrites          | ❌ Never                           | Latest value only             | UI state, latest config
* */


suspend fun channelTypeRendezvous(): Unit= coroutineScope {
    val channel = produce {
        // or produce(capacity = Channel.RENDEZVOUS) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }
    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

/*
1. Channel() – Rendezvous (default)
    No buffer at all.
    send() suspends until another coroutine calls receive().
    Great for real-time handshakes between coroutines.

Example: It means that an exchange can happen only if sender and receiver meet
(so it is like a book exchange spot, instead of a bookshelf).

* */


suspend fun channelTypeBufferred(): Unit= coroutineScope {
    val channel = produce(capacity = 3) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }
    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

/*
    🔎 2. Channel(capacity = N) – Buffered
            Stores up to N items.
            send() suspends only when full.
            Allows decoupling producer & consumer a bit.

With a capacity of concrete size, we will first produce until the buffer is full, after which the producer will
need to start waiting for the receiver.

Here, once the 3 elements are in channel, it suspends as buffer capacity is full and receiver had not consumed any
elements(as delay is 1 second, while for send delay is only 100 milliseconds), So once receiver consume elements, then
only buffer will be empty and then only sender can send elements
* */



suspend fun channelTypeConflated(): Unit= coroutineScope {
    val channel = produce(capacity = Channel.CONFLATED) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }
    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

/*
    Channel.CONFLATED
        Only keeps the most recent value.
        Each new send() overwrites the old value.
        send() never suspends.
        receive() gets the latest.

Finally, we will not be storing past elements when using the Channel.CONFLATED capacity. New elements will replace the
previous ones, so we will be able to receive only the last one, therefore we lose elements that were sent earlier.
* */


suspend fun channelTypeUnlimited(): Unit= coroutineScope {
    val channel = produce(capacity = Channel.UNLIMITED) {
        repeat(5) { index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }
    delay(1000)
    for (element in channel) {
        println(element)
        delay(1000)
    }
}

/*
    Channel.UNLIMITED
        Infinite buffer (backed by a resizable ArrayList).
        send() never suspends.
        Risk: Can lead to memory leaks if producer is faster than consumer.

With unlimited capacity, the channel should accept all the elements and then let them be received one after another
* */


suspend fun channelTypeCustom(): Unit= coroutineScope {
    val channel = Channel<Int>(
        capacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    launch {
        repeat(10) { index ->
            val element = index * 2
            channel.send(element)
            println("Suspending for 400ms")
            delay(400)
            println("Sent:: $element")
        }
        channel.close()
    }

    delay(150)
    for (element in channel) {
        println("Received:: $element, suspending for 1s")
        delay(1000)
    }
}

/*
* Heer, we are making custom buffered channel(133). It will behave same as Channel.CONFLATED. It sets the capacity to 2,
and when it is full, it removes the previous oldest value from the queue(due to line 135)
* */

 fun main(): Unit = runBlocking {
     //channelTypeRendezvous()
     // channelTypeBufferred()
     //channelTypeConflated()
     //channelTypeUnlimited()
     channelTypeCustom()
}