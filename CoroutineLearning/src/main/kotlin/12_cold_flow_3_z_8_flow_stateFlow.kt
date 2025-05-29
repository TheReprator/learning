package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
✅ What is StateFlow?
    1)StateFlow is a state-holder observable flow, which is built on top of SharedFlow, and always has a current value.
    2)Think of StateFlow as a reactive version of a val that you can observe.

🔁 Behavior of StateFlow
    Always emits the latest value to new collectors.
    Only emits changes — it won't emit the same value again unless it's different.
    Never completes or throws (unless explicitly canceled).
    Designed for UI-state propagation in Jetpack Compose or Android MVVM.

So you can think of StateFlow as:
    SharedFlow + current value
* */

private suspend fun hotFlow_stateFlow(): Unit= coroutineScope {
    val state = MutableStateFlow("A")
    println(state.value)
    launch {
        state.collect { println("Collector #1, Value changed to $it") }
    }
    delay(1000)
    state.value = "B"
    delay(1000)
    launch {
        state.collect { println("Collector #2, and now it is $it") }
    }
    delay(1000)
    state.value = "C" // Value changed to C and now it is C
}

private suspend fun hotFlow_stateFlow_stateIn_type1(): Unit= coroutineScope {
    val flow = flowOf("A","B","C")
        .onEach { delay(1000) }
        .onEach { println("Produced $it") }
    val stateFlow: StateFlow<String> = flow.stateIn(this)
    println("Listening")
    println(stateFlow.value)
    stateFlow.collect { println("Received $it") }
}

/*
Notes:
    1) stateIn is a function that transforms Flow<T> into StateFlow<T>. It can only be called with a scope, but it is a
       suspending function. Remember that StateFlow needs to always have a value; so, if we don’t specify it, then we need
       to wait until the first value is calculated.
    2) This variant of stateIn is suspending

Output:
    (//1 Second)
    Produced A
    Listening
    A
    Received A
    Produced B
    Received B
    Produced C
    Received C
    (//Runs forever)

Observation:
    Here we have to wait for 1 second for the first output, as stateflow should have 1 value, Since, we hadn't provided
    the 1st value, then we have wait until the 1st element are produced, and hence this statein is suspending in nature
* */

private suspend fun hotFlow_stateFlow_stateIn_type2(): Unit= coroutineScope {
    val flow = flowOf("A","B")
        .onEach { delay(1000) }
        .onEach { println("Produced $it") }
    val stateFlow: StateFlow<String> = flow.stateIn(
        scope = this,
        started = SharingStarted.Lazily,
        initialValue = "Empty"
    )
    println(stateFlow.value)
    delay(2000)
    stateFlow.collect { println("Received $it") }
}

/*
Notes:
    The second variant of stateIn is not suspending but it requires an initial value and a started mode. This mode has
    the same options as shareIn (as previously explained, 12_cold_flow_3_z_7_flow_sharedFlow.kt).

Output:
    Empty
    Received Empty
    Produced A
    Received A
    Produced B
    Received B
* */

fun main(): Unit = runBlocking {
/*    println("Hot Flow-->")
    println("StateFlow-->")
    hotFlow_stateFlow()
    println()
    println()*/
    println("StateFlow stateIn type1-->")
    hotFlow_stateFlow_stateIn_type1()
    println()
    println()
   /* println("StateFlow stateIn type2-->")
    hotFlow_stateFlow_stateIn_type2()
    println()
    println()*/
}