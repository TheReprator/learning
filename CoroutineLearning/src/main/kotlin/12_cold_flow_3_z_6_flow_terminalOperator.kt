package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/*

🧠 All Three at a Glance
    Operator	    Type	        Emits intermediate values?	Initial value?	    Final result type
    reduce	        Terminal	    ❌ No	                    ❌ No	            Final reduced value
    fold	        Terminal	    ❌ No	                    ✅ Yes	            Final folded value
    scan	        Intermediate	✅ Yes (including initial)	✅ Yes	            Flow of all values


* */
private fun flow_terminal_reduce() = runBlocking {
    val list = flowOf(1, 2, 3, 4)
        .onEach { delay(1000) }
    val res = list.reduce { acc, i -> acc + i }
    println(res)
}

/*
Reduce:
    Accumulates values in the flow from the first item onward, returns only the final result.
🔸 Requires at least 1 value — otherwise, it throws NoSuchElementException.


Output:
    10
* */

private fun flow_terminal_fold() = runBlocking {
    val list = flowOf(1, 2, 3, 4)
        .onEach { delay(1000) }
    val res = list.fold(0) { acc, i -> acc + i }
    println(res)
}

/*
Fold: Works like reduce, but starts with an initial value (identity).
* */

private suspend fun flow_terminal_launchIn() = coroutineScope {
    flowOf("User1"
        ,
        "User2")
        .onStart { println("Users:") }
        .onEach { println(it) }
        .launchIn(this)
}

/*
launchIn(scope):
    1) It is a terminal operator that starts collecting a flow in a given CoroutineScope, without suspending
        the current coroutine.
    2) Think of it as a way to fire-and-forget the flow collection asynchronously.

🧠 Use Cases
    Collect a flow inside a ViewModel (with viewModelScope)
    Reactively listen to UI events
    Use with StateFlow, SharedFlow, or Flow with side effects

🔄 Compared to collect
    Feature	                collect { }	            launchIn(scope) + onEach
    Suspends caller	        ✅ Yes	                ❌ No (runs in background)
    Use for side effects	✅ Yes	                ✅ Yes (requires onEach)
    Returns	                Unit	                Job (so you can cancel it)
    Use case	            Inline collection	    Background/reactive collection
* */

fun main(): Unit = runBlocking {
    println("Terminal-->")
    println("reduce-->")
    flow_terminal_reduce()
    println()
    println()
    println("Fold-->")
    flow_terminal_fold()
    println()
    println()
    println("launchIn-->")
    flow_terminal_launchIn()
}

