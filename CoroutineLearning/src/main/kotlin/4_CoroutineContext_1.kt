package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main()  {
    val ctx: CoroutineContext = CoroutineName("mainVikram")
    val coroutineName = ctx[CoroutineName]
    println(coroutineName?.name)

    val job: Job? = ctx[Job]
    println(job)
}