package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
* Flow is typically cold, so its values are calculated on demand. However, there are cases in which we want multiple
receivers to be subscribed to one source of changes. This is where we use SharedFlow

SharedFlow: A hot, broadcast-style flow that supports multiple collectors, similar to RxJava's PublishSubject or LiveData,
    but more powerful.
* */

private suspend fun hotFlow_sharedFlow(): Unit= coroutineScope {
    val mutableSharedFlow = MutableSharedFlow<String>()

    launch {
        mutableSharedFlow.collect {
            println("#1 received $it")
        }
    }
    launch {
        mutableSharedFlow.collect {
            println("#2 received $it")
        }
    }
    delay(1000)
    mutableSharedFlow.emit("Message1")
    mutableSharedFlow.emit("Message2")
}

/*
Output:
    // (1 sec)
    #1 received Message1
    #2 received Message1
    #1 received Message2
    #2 received Message2
    //(program never ends)

Observation:
    The above program never ends because the coroutineScope is waiting for the coroutines that were started with launch
and which keep listening on MutableSharedFlow. Apparently, MutableSharedFlow is not closable, so the only way to fix
this problem is to cancel the whole scope.
* */

private suspend fun hotFlow_sharedFlow_replay(): Unit= coroutineScope {
    val mutableSharedFlow = MutableSharedFlow<String>(replay = 2)
    launch {
        mutableSharedFlow.collect {
            println("Collector A $it")
        }
    }
    delay(100)
    mutableSharedFlow.emit("Message1")
    mutableSharedFlow.emit("Message2")
    mutableSharedFlow.emit("Message3")
    println(mutableSharedFlow.replayCache)      // [Message2, Message3]

    launch {
        mutableSharedFlow.collect {
            println("Collector B $it")
        }
    }
    delay(100)
    mutableSharedFlow.resetReplayCache()
    println(mutableSharedFlow.replayCache) // []
}

/*
Notes:
    If we set the replay parameter (it defaults to 0), the defined number of last values will be kept. If a coroutine now
starts observing, it will receive these values first. This cache can also be reset with resetReplayCache.

Output:
    Collector A Message1
    Collector A Message2
    Collector A Message3
    [Message2, Message3]
    Collector B Message2
    Collector B Message3
    []
 Observation:
    1)Collector B missed Message0 — because it's hot and started late

    2) Notice although values is already emitted(line 58-60), but when we start collect(line 66), it still receives the last
    2 emitted values due to replay cache of 2(line 51)
* */

/*
🔄 What is shareIn?
    The shareIn operator in Kotlin Flow is used to convert a cold flow into a hot shared flow. As we know, when a normal flow
is collected multiple times, it executes independent and separate for each collect. So, to avoid re-execution when
multiple collectors observe the same flow, we have to convert it into hot flow(shared flow)

shareIn:
    1)Shares a cold Flow as a SharedFlow
    2)Starts the flow in a given coroutine scope
    3)Avoids re-triggering the upstream on each collector
    4)Caches/replays emissions depending on configuration
* */
private suspend fun hotFlow_sharedFlow_shareIn(): Unit= coroutineScope {
    val flow = flowOf("A","B", "C")
        .onEach { delay(1000) }
    val sharedFlow: SharedFlow<String> = flow.shareIn(
        scope = this,
        started = SharingStarted.Eagerly
    )
    delay(500)
    launch {
        sharedFlow.collect { println("#1 $it") }
    }
    delay(1000)
    launch {
        sharedFlow.collect { println("#2 $it") }
    }
    delay(1000)
    launch {
        sharedFlow.collect { println("#3 $it") }
    }
}
/*
Output:
    #1 A
    #1 B
    #2 B
    #1 C
    #2 C
    #3 C

Note:
    1)One execution of flow, shared between collectors due to line 113, As we can see Collector A,B,C missed 0,1,2 element
      respectively.
    2)Listening mode Eagerly is used(line 115),
         It immediately starts listening for values and sending them to a flow. Notice that if you have a limited replay
      value and your values appear before you start subscribing, you might lose some values (if your replay is 0, you
      will lose all such values). Due to this only, Collector B,C missed 1,2 element respectively.
* */

private suspend fun hotFlow_sharedFlow_shareIn_lazily(): Unit= coroutineScope {
    val flow1 = flowOf("A","B","C")
    val flow2 = flowOf("D")
        .onEach {
            delay(1000)
        }
    val sharedFlow = merge(flow1, flow2).shareIn(
        scope = this,
        started = SharingStarted.Lazily,
    )
    delay(100)
    launch {
        sharedFlow.collect { println("#1 $it") }
    }
    delay(1000)
    launch {
        sharedFlow.collect { println("#2 $it") }
    }
}

/*
Output:
    #1 A
    #1 B
    #1 C
    #1 D
    #2 D
    //Never Ending
Notes:
    • SharingStarted.Lazily - starts listening when the first subscriber appears. This guarantees that this first
      subscriber gets all the emitted values, while later subscribers are only guaranteed to get the most recent
      replay values. The upstream flow continues to be active even when all subscribers disappear

    • If we comment line 153, then Collect #2, would have never received anything, as started mode is SharingStarted.Lazily,
      and replay cache is also by 0, So, till the time collector #2 started, all values are already emitted, hence nothing
      is there to be collected
* */

private suspend fun hotFlow_sharedFlow_shareIn_WhileSubscribed(): Unit= coroutineScope {
    val flow = flowOf("A","B","C","D")
        .onStart { println("Started") }
        .onCompletion { println("Finished") }
        .onEach { delay(1000) }

    val sharedFlow = flow.shareIn(
        scope = this,
        started = SharingStarted.WhileSubscribed(),
    )
    delay(3000)
    launch {
        println("#1 ${sharedFlow.first()}")
    }

    launch {
        println("#2 ${sharedFlow.take(2).toList()}")
    }
    delay(3000)
    launch {
        println("#3 ${sharedFlow.first()}")
    }
}

/*
Output:
    Started
    #1 A
    #2 [A, B]
    Finished
    Started
    #3 A
    Finished

Notes:
    1) WhileSubscribed() - It starts listening on the flow when the first subscriber appears; it stops when the last
       subscriber disappears. If a new subscriber appears when our SharedFlow is stopped, it will start again.
       WhileSubscribed has additional optional configuration parameters: stopTimeoutMillis (how long to listen after the
       last subscriber disappears, 0 by default) and replayExpirationMillis (how long to keep replay after stopping,
       Long.MAX_VALUE by default).
        1.a) Think of it as:
                “Start producing data only when someone is listening, and stop when no one is.”
        1.b)Behavior:
                Starts collecting only when a collector appears
                Cancels upstream after last collector is gone
                Will restart again if a new collector subscribes

Observation:
    You can see, no "Started" was called for collect #2, and no "Finished" was called for Collector #1, as they share the
    same upstream(multiple collectors are subscribed at the same time, they receive values from the same shared upstream).

* */

fun main(): Unit = runBlocking {
    println("Hot Flow-->")
    /* println("SharedFlow-->")
     hotFlow_sharedFlow()
     println()
     println()
     println("SharedFlow Replay-->")
     hotFlow_sharedFlow_replay()
     println()
     println()
     println("SharedFlow ShareIn(Eagerly)-->")
     hotFlow_sharedFlow_shareIn()
     println()
     println()
     println("SharedFlow ShareIn(Lazily)-->")
     hotFlow_sharedFlow_shareIn_lazily()
     println()
     println()*/
    println("SharedFlow ShareIn(WhileSubscribed)-->")
    hotFlow_sharedFlow_shareIn_WhileSubscribed()
    println()
    println()
}