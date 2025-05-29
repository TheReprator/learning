package org.example

import kotlinx.coroutines.*

fun main() = runBlocking {
    val limitedDispatcher = Dispatchers.Default.limitedParallelism(5)

    repeat(10) { i ->
        launch(limitedDispatcher) {
            println("Task $i started on thread: ${Thread.currentThread().name}")
            delay(1000) // pretend this task takes 1 second
            println("Task $i finished")
        }
    }
}

/*
Limiting the default dispatcher: I want only 5 coroutines running at once, even if I start 100 coroutines.

Output:
        Task 0 started
        Task 1 started
        Task 2 started
        Task 3 started
        Task 4 started
        (wait 1 second)
        Task 0 finished
        Task 1 finished
        Task 2 finished
        Task 3 finished
        Task 4 finished
        Task 5 started
        Task 6 started
        Task 7 started
        Task 8 started
        Task 9 started
        (wait 1 second)
        Task 5 finished
        Task 6 finished
        Task 7 finished
        Task 8 finished
        Task 9 finished

Keypoints from output:
    Only 5 tasks are running at the same time.
    When the first 5 tasks finish, the next 5 tasks start.
    Even though you launch all 10 coroutines immediately, Kotlin controls how many are actually active at once(in this
    example max 5 coroutines will be active at a time)
 */