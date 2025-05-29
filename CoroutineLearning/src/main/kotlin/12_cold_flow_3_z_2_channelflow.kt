package org.example

import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
ChannelFlow: It is a special kind of Flow that lets you emit values from multiple coroutines concurrently, safely, using
    channels under the hood.

🔥 Why was ChannelFlow introduced?
    In normal flow {},
        Emission must happen from only one coroutine — the one building the flow.
        If you try to emit from another coroutine → it crashes (IllegalStateException).
    So Kotlin introduced channelFlow { } where:
        Multiple coroutines can safely emit into the same flow.
        Behind the scenes, it uses a Channel (kind of like a thread-safe queue).
* */

private fun flowVariant() = runBlocking {
    val flow = flow {
        launch {
            emit(1) // ❌ ILLEGAL! Crash here
        }
    }

    flow.collect { println(it) }
}


private fun channelFlowVariant() = runBlocking {
    val flow = channelFlow {
        launch {
            send(1) // ✅ SAFE
        }
        launch {
            send(2) // ✅ SAFE
        }
    }

    flow.collect { println(it) }
}

/*
If you run the functions flowVariant(line 21), you will get following exception:
    Exception: Flow invariant is violated

To overcome the issue of flow(to listen from multiple coroutine), we use channelFlow(channelFlowVariant)
    output:
        1
        2

🎯 So in short:
    Aspect	                    flow {}	            channelFlow {}
    Multi-coroutine emission	❌ Crash	        ✅ Safe
    Underlying Mechanism	    Simple sequential	Buffered Channel (queue)
    Use Case	                Simple linear flow	Complex concurrent emissions

🔥 When should you use channelFlow?
    Use channelFlow when:
        You want multiple background coroutines to send data into one flow.
        You need concurrent operations emitting into a single stream.
        Example: Downloading multiple files in parallel and emitting progress updates.

🧠 Quick mental model:
    flow { }	                            channelFlow { }
    Like a person passing a note quietly	Like a post office accepting many letters at once

Inside channelFlow we operate on ProducerScope<T>. ProducerScope is the same type as used by the produce builder.
It implements CoroutineScope, so we can use it to start new coroutines
* */
fun main() {
    //flowVariant()
    channelFlowVariant()
}



