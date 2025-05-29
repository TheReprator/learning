package org.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
onEmpty: It is an intermediate operator in Kotlin Flow used to react when a flow emits no values — i.e., it completes
        without emitting anything.

        It emits only when program or function is supposed to be exit
* */
private fun flow_intermediate_onEmpty() = runBlocking {
    //flowOf<Int>(1)
    flowOf<Int>()
        .onEmpty {
            println("Flow was empty!")
        }.collect {
            println("Collected: $it")
        }
}

/*
Output:
    Flow was empty!

If we uncomment line 14 and comment line 15, then "OnEmpty" will not be called
* */

private fun flow_intermediate_1_map() = runBlocking {
    flowOf(1, 2, 3)
        .map {
            it * 2
        }.collect {
            println("Collected: $it")
        }
}

/*
A function that takes an element from the source Flow and returns a new Flow or sequence of elements representing the transformed output.

Transforms each value emitted by the flow.
* */

private fun flow_intermediate_2_filter() = runBlocking {
    flowOf(1, 2, 3, 4, 5)
        .filter { it % 2 == 0 }
        .collect {
            println("Collected: $it")
        }
}

/*
Filters values based on a predicate.
* */

private fun flow_intermediate_3_transform() = runBlocking {
    flowOf(1, 2)
        .transform {
            emit("Before $it")
            emit("After $it")
        }
        .collect {
            println("Collected: $it")
        }
}

/*
Transform:
    1) It is an intermediate flow operator that lets you emit any number of values (including none) for each upstream
        emission, using arbitrary logic.
    2) Think of it as a general-purpose tool that can replace map, filter, flatMap, etc., all in one. As all internally
       uses transform operator
    3) Since it uses, FlowCollector as lambda, that's why we can use emit from inside body of transform
* */

private fun flow_intermediate_4_take() = runBlocking {
    flowOf(1, 2,3,4,5)
        .take(3)
        .collect {
            println("Take Collected: $it")
        }
}

/*
take(n): Takes first n elements
* */

private fun flow_intermediate_5_drop() = runBlocking {
    flowOf(1, 2,3,4,5)
        .take(3)
        .collect {
            println("Take Collected: $it")
        }
}

/*
drop(n): Skips first n elements
* */

private fun flow_intermediate_6_onEach() = runBlocking {
    flowOf(1, 2, 3, 4)
        .onEach {
            it*it
            //delay(1000)
        }
        .collect {
            print(it)
        }
}

/*
The onEach lambda expression is suspending, and elements are processed one after another in order (sequentially).
So, if we add delay in onEach, we will delay each value as it flows.

Usage:
    Perform side effects (like logging) for each value.

Output:
        1234
    In line 103, we can see we are multiplying every no by itself, so in collect(line 107), the output should be like
    1,2,9,16, but the output is 1234, so it proves the point(line 116), that onEach should be used only for logging
    or side effects
* */

/*
take(n): Takes first n elements
* */

private fun flow_intermediate_6_debounce() = runBlocking {
    flow {
        emit(1)
        delay(100)
        emit(2)
        delay(300)
        emit(3)
    }.debounce(200)
        .collect { println(it) }
}

/*
debounce: Emits value only if no new value has arrived in the given time.
Output:
    2
    3
* */

private fun flow_intermediate_6_scan() = runBlocking {
    flowOf(1, 2, 3, 4)
    .scan(0) { acc, value -> acc + value }
        .collect {
            println(it)
        }
}

/*
Scan: Accumulates values like reduce, but emits each intermediate result. And it is a intermediate operator whereas
    reduce is a terminal operator
Output:
    0
    1
    3
    6
    10

Observation:
    Notice println(line 152) in collect, is printed 5 times but flow has only 4 elements. It is so because default
    elements are also emitted with scan, that is why we go 5 elements as 0 is also emitted
* */

private fun flow_intermediate_7_zip() = runBlocking {
    val numbers = flowOf(1, 2, 3).onEach { delay(200) }
    val letters = flowOf("A", "B", "C", "D", "E").onEach { delay(500) }

    numbers.zip(letters) { num, char ->
        "$num$char"
    }.collect {
        println(it)
    }
}

/*
Zip: Combines two flows pair-wise. It makes pairs from both flows. It also need to specify a function that decides
    how elements are paired(lin 176). Each element can only be part of one pair, so it needs to wait for its pair.
    Elements left without a pair are lost, that's why element D, E from flow letters(line 173) will not appear in result
    as there is no corresponding elements in the numbers flow(line 172)
Output:
    1A
    2B
    3C

* */

private fun flow_intermediate_8_merge() = runBlocking {
    val ints: Flow<Int> = flowOf(1, 2, 3)
        .onEach { delay(1000) }
    val doubles: Flow<Double> = flowOf(0.1, 0.2, 0.3)
    val together: Flow<Number> = merge(ints, doubles)
    together.collect { println(it) }
}

/*
Merge: The elements from one flow, do not wait for another flow.

🟡 merge – Concurrent emission
    🔹 What it does:
        Emits values from multiple flows as they arrive, interleaved, in real-time.
        Think of it as "merge all values into one timeline, as soon as they emit".

🧠 merge does not wait for both flows to emit — it just takes whatever is ready first.

Output:
    0.1
    0.2
    0.3
    1
    2
    3

Observation:
    elements from the ints flow(line 195) are delayed, but this does not stop the elements from the second
    doubles flow(197) to be emitted, this proves point(205, 206)
* */

private fun flow_intermediate_9_combine() = runBlocking {
    val flow1 = flowOf("A","B","C")
        .onEach { delay(400) }
    val flow2 = flowOf(1, 2, 3, 4)
        .onEach { delay(1000) }
    flow1.combine(flow2) { f1, f2 -> "${f1}_${f2}" }
        .collect { println(it) }
}

/*
Combine:
    1)When we use combine,every new element replaces its predecessor. If the first pair has been formed already,
        it will produce a new pair together with the previous element from the other flow.

    2) Notice that zip needs pairs, so it closes when the first flow closes. combine does not have such a limitation,
        so it will emit until both flows are closed.

🟢 combine – Latest value pairing
    🔹 What it does:
        Combines latest values from both flows whenever any flow emits.
        Waits for at least one value from each flow, then emits a pair/computed result.

🧠 combine emits only when both flows have at least one value, and then every time any one flow emits after that.

Output:
    B_1
    C_1
    C_2
    C_3
    C_4

Observation:
    element A is never printed, as combine needs one element from both flows, So, when element 1 is emitted from flow2
    after a second, then till that time element B has already been emitted from flow1, that has replaced it's predecessor
    A, so combine holds element B till that, this proves point 1(line 236)

🧠 Analogy:
    merge: Two people talking randomly — you hear both as they speak.
    combine: Two people building a sentence together — you only hear output when both have contributed.
* */

private fun flow_intermediate_10_flatMapConcat() = runBlocking {
    fun flowFrom(elem: String) = flowOf(1, 2)
        .onEach { delay(1000) }
        .map { "${it}_${elem}" }

    flowOf("A","B")
        .flatMapConcat { flowFrom(it) }
        .collect { println(it) }
}

/*
flatMapConcat: Flattens flows sequentially
📌 Description:
    Waits for the inner flow to finish before starting the next.
    Preserves order.

🧠 Use when:
    You want strict order.
    You don’t care about speed as much as predictability.

Output:
    1_A
    2_A
    1_B
    2_B
* */

private fun flow_intermediate_10_flatMapMerge() = runBlocking {
    fun flowFrom(elem: String) = flowOf(1, 2)
        .onEach { delay(1000) }
        .map { "${it}_${elem}" }

    flowOf("A","B")
        .flatMapMerge { flowFrom(it) }
        .collect { println(it) }
}

/*
flatMapMerge: Flattens flows concurrently
📌 Description:
    Starts all inner flows immediately and collects results as they arrive.

🧠 Use when:
    You want fastest possible results.
    Order doesn't matter, or flows are independent.

Output:
    1_A
    1_B
    2_A
    2_B

Note: Order might vary depending on delay and execution.
* */

private fun flow_intermediate_10_flatMapLatest() = runBlocking {
    fun flowFrom(elem: String) = flowOf(1, 2)
        .onEach { delay(1000) }
        .map { "${it}_${elem}" }

    flowOf("A","B")
        .flatMapLatest { flowFrom(it) }
        .collect { println(it) }
}

/*
flatMapLatest: Cancels previous flow on new value(Only the latest value's full flow is collected. Previous ones get
               cancelled if still running.)


📌 Description:
    Cancels the previous inner flow if a new value is emitted.
    Only emits from the latest inner flow.

🧠 Use when:
    You need to reflect only the latest value/state (like search suggestions, UI updates).
    You want to cancel old work (e.g., user changed input quickly).

Output:
    1_B
    2_B
* */

/*
🧠 Real-world analogy:
    Scenario	                        Operator
    Sending tasks one-by-one	        flatMapConcat
    Running all tasks in parallel	    flatMapMerge
    Aborting old task, keeping latest	flatMapLatest

* */

private fun flow_intermediate_11_retry() = runBlocking {
    var attempt = 0
    flow {
        attempt++
        if (attempt < 3) throw RuntimeException("Failing...")
        emit("Success on attempt $attempt")
    }.retry(2) // Will retry up to 2 times after failure
        .catch {
            emit("Fallback")
        }.collect {
            println(it)
        }
}

/*
retry: It is an intermediate operator that:
    Intercepts exceptions during upstream emissions,
    Restarts the flow, based on custom logic or a fixed retry count.

🧩 Types of Retry Operators
    Operator	    Behavior
    retry(n)	    Retries up to n times on any failure
    retry(n) { }	Retries up to n times, but only if the predicate returns true
    retry { }	    Retries infinitely as long as the predicate returns true

⚠️ When Does Retry NOT Work?
    Retry only applies to exceptions thrown during flow collection.
    It does not retry on:
        CancellationException
        Emission errors inside collect {} block — those must be handled manually.

Only cold flows benefit from retry, because hot flows (like SharedFlow) are not restarted on retry.

Output:
    Success on attempt 3

* */

private fun flow_intermediate_11_retry_notWorking() = runBlocking {
    var attempt = 0
    flow {
            attempt++
            if (attempt < 3) throw RuntimeException("Failing...")
            emit("Success on attempt $attempt")
        }.catch {
            emit("Fallback")
        }.retry(2) // Will retry up to 2 times after failure
        .collect {
            println(it)
        }
}

/*
Output:
    Fallback

Observation:
    Here, no value is emitted(Success on attempt) from flow body(line 398-401), due to following reason

❓ Why doesn't retry(2) work here?
    Because you're placing .catch { ... } before .retry(2), which means:
        The catch block absorbs the exception, so the retry block never sees it.
        Once an error is caught, it's no longer considered a failure, so retry has nothing to retry.
* */


private fun flow_intermediate_12_flowon() = runBlocking {

    flow {
        withContext(Dispatchers.IO) {
            emit(2) // ❌ Wrong — will throw exception
        }
    }
    /*
    It will throw exception once we apply terminal operator on this flow, as changing context is not allowed within
        body of flow.
    ⚠️ Flow builder must not emit from a different context — that’s what flowOn is for.
    * */

    flow {
        println("Start: ${Thread.currentThread().name}")
        emit(1)
    }.map {
        println("Map: ${Thread.currentThread().name}")
        it * 2
    }.flowOn(Dispatchers.IO)
        .map {
            println("Map2: ${Thread.currentThread().name}")
            it + 1
        }.flowOn(Dispatchers.Default)
        .collect {
            println(it)
        }
}

/*
flowOn(context): It changes the coroutine context (dispatcher) for the upstream flow — meaning everything above flowOn
    in the flow chain will run in the specified coroutine context (like Dispatchers.IO, Dispatchers.Default, etc.).
* */


fun main(): Unit = runBlocking {
    println("onEmpty-->")
    flow_intermediate_onEmpty()
    println()
    println()
    println("Map-->")
    flow_intermediate_1_map()
    println()
    println()
    println("Filter-->")
    flow_intermediate_2_filter()
    println()
    println()
    println("Transform-->")
    flow_intermediate_3_transform()
    println()
    println()
    println("Take-->")
    flow_intermediate_4_take()
    println()
    println()
    println("Drop-->")
    flow_intermediate_5_drop()
    println()
    println()
    println("OnEach-->")
    flow_intermediate_6_onEach()
    println()
    println()
    println("Debounce-->")
    flow_intermediate_6_debounce()
    println()
    println()
    println("Scan-->")
    flow_intermediate_6_scan()
    println()
    println()
    println("Zip-->")
    flow_intermediate_7_zip()
    println()
    println()
    println("Merge-->")
    flow_intermediate_8_merge()
    println()
    println()
    println("Combine-->")
    flow_intermediate_9_combine()
    println()
    println()
    println("flatMapConcat-->")
    flow_intermediate_10_flatMapConcat()
    println()
    println()
    println("flatMapMerge-->")
    flow_intermediate_10_flatMapMerge()
    println()
    println()
    println("flatMapLatest-->")
    flow_intermediate_10_flatMapLatest()
    println()
    println()
    println("retry-->")
    flow_intermediate_11_retry()
    println()
    println()
    println("retry notWorking-->")
    flow_intermediate_11_retry_notWorking()
    println()
    println()
    println("flowon-->")
    flow_intermediate_12_flowon()
}

