package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
 🎯 What is callbackFlow?
    callbackFlow is used when you want to convert a callback-based API (like listeners, events, etc.) into a Flow.
    ✅ It's like a bridge between traditional async callback world ➡️ suspending flow world.
    ✅ Internally, callbackFlow is built on ChannelFlow — so it allows multi-coroutine safe emissions too.

📜 Internally How callbackFlow Works
    It creates a Channel.
    It exposes trySend to push data into the channel.
    awaitClose {} is used to clean up.
    When you collect, it reads from the channel.

* */

fun interface Listener {
    fun onEvent(value: String)
}

private fun registerListener(scope: CoroutineScope, listener: Listener) {
    // Imagine it's calling back after 1 sec
    scope.launch {
        delay(1000)
        listener.onEvent("First Event after 1 second")
        delay(1000)
        listener.onEvent("Second Event after 2 seconds")
    }
}

private fun flowCallback(): Flow<String> = callbackFlow {
    val listener = Listener { value ->
        trySend(value) // send value into the flow
        if (value == "Second Event after 2 seconds") {
            close() // 👈 Close the flow manually
        }
    }

    registerListener(this, listener)

    awaitClose {
        println("Flow closed")
        // Clean up if needed
    }
}

/*
 Output:
        First Event after 1 second
        Second Event after 2 seconds

 Here at line 44, i had used trySend as it is not suspending function else we have to use coroutine scope on whole function
 At line 46, We had manually closed the flow

 🧠 Key Points about callbackFlow
    Feature	                Meaning
    Used for	            Turning callbacks/listeners into flows
    Emission Safety	        Multi-coroutine safe
    Needs awaitClose?	    ✅ Yes (otherwise leak risk)
    Based on	            ChannelFlow internally
    Can use send/trySend	✅ Yes

📦 Why do we need awaitClose inside callbackFlow?
    Because listeners are usually infinite (they keep calling back anytime).
    If the collector cancels / closes the flow, we must unregister the listener manually to prevent memory leaks.
    awaitClose {} is called when the flow is no longer collected

🧠 Difference between flow, channelFlow, and callbackFlow
                                flow {}	                        channelFlow {}	                    callbackFlow {}
    Built for	                Simple suspending emission	    Concurrent coroutine emissions	    Callback-to-Flow conversion
    Single coroutine emission?	✅ Yes	                        ❌ No (multi allowed)	            ❌ No (multi allowed)
    Multi coroutine emission?	❌ Crash	                    ✅ Safe	                            ✅ Safe
    Needs awaitClose?	        ❌ No	                        ❌ No	                            ✅ Yes
    Heavyweight?	            ❌ No	                        ⚡ A bit	                            ⚡ A bit
    Example use	                for-loops, simple delays	    multiple parallel jobs	            API listeners, event bus, broadcast receivers


🎯 TL;DR:
Use callbackFlow when you need to turn callback/listener/event APIs into Kotlin Flows, safely and cleanly.
    ✅ callbackFlow is built over channelFlow.
    ✅ Always remember to awaitClose {} for cleanup.
    ✅ Use trySend inside it, not emit.

🔥 Bonus Mnemonic Trick to Remember
    Think	            Meaning
    flow {}	            ➡️ "I control the emission."
    channelFlow {}	    ➡️ "Many workers control emission."
    callbackFlow {}	    ➡️ "Outside world controls emission (via callbacks)."
* */
fun main(): Unit = runBlocking {
    flowCallback().collect { value ->
        println("Received: $value")
    }
}

