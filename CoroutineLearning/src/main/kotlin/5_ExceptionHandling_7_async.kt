package org.example

import kotlinx.coroutines.*


private suspend fun type1(): Unit = coroutineScope {
    val asyncException = async {
        delay(500)
        throw Exception("type1: exception")
    }
}

/*
* As we know, async is already in active state by default until unless we explicity mark it
* as ( async(start = CoroutineStart.LAZY) ). So, it will crash the program as soon as it met
* an exception(line 9)
* */

private suspend fun type2(): Unit = supervisorScope {
    val asyncException = async {
        delay(500)
        throw Exception("type2: Exception")
    }

    val asyncNormal = async {
        delay(600)
        println("type2: normal")
    }
}

/*
* At line 22, exception is throw but is silenced(line 20) as we are using coroutines cope, and it doesn't
* affect the other builder(line 25)
* */

private suspend fun type3(): Unit = supervisorScope {
    val asyncException = async<String> {
        delay(500)
        throw Exception("type3: Exception")
    }

    val asyncNormal = async {
        delay(600)
        "type3: normal"
    }

    try {
        println(asyncException.await())
    }catch (e: Exception) {
        println(e.message)
    }
    println(asyncNormal.await())
}

/*
* A coroutine that was created using async always catches all its exceptions and represents them in the resulting
* Deferred object, so it cannot result in uncaught exceptions.
* If you use async, silencing its exception propagation to the parent is not enough. When we call await and the async
* coroutine finishes with an exception, then await will rethrow it.
*
* When exception is thrown(line 39), it is silenced, due to use of supervisor scope(line 36) as parent
* On using await with async(line 48), it throws the exception, that's why we use the try catch(line 47), else
* it will crash the application. Due to use of try catch with asyncException.await, asyncNormal.await is not
* affected
* */

fun main(): Unit = runBlocking {
    //type1()
    //type2()
    type3()
}
