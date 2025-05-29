package org.example

import kotlinx.coroutines.*

private fun superVisorScopeWithContextType1(): Unit = runBlocking {
    withContext(SupervisorJob()) {
        launch {
            delay(700)
            println("superVisorScopeWithContextType1: SupervisorScope child")
        }

        launch {
            delay(1000)
            throw Exception("superVisorScopeWithContextType1: SupervisorScope child Exception")
        }

        launch {
            delay(1200)
            println("superVisorScopeWithContextType1: SupervisorScope coroutine builder")
        }
    }

    launch {
        delay(1500)
        println("superVisorScopeWithContextType1: parent main builder")
    }

    launch {
        delay(500)
        println("superVisorScopeWithContextType1: coroutine builder")
    }
}

/*
WithContext: It is basically a function, which is used to change the coroutine context. It is not builders like
        async or launch which is used to create a coroutine.

withContext(SupervisorJob())(Line 6) does not create a SupervisorScope. Instead, it inherits the parent scope
(which is runBlocking in this case, line 5).

When any child coroutine inside withContext throws an exception(line 14), it cancels all sibling coroutines(line 17, line7(already complete))
and propagates the exception up to the parent runBlocking scope(line 5), which then cancel all its children(lin 23,
not line 28(already complete)) and then itself, and then throws the exception to caller main, which causes the program
to crash

We can omit line 24, as WithContext run on same builder, so no need to wait.
*/


private suspend fun superVisorScopeWithContextType2() = coroutineScope {
    withContext(Dispatchers.IO) {
        supervisorScope {
            launch {
                delay(700)
                println("superVisorScopeWithContextType2: SupervisorScope child")
            }

            launch {
                delay(1000)
                throw Exception("superVisorScopeWithContextType2: SupervisorScope child Exception")
            }

            launch {
                delay(1200)
                println("superVisorScopeWithContextType2: SupervisorScope coroutine builder")
            }
        }
    }

    launch {
        delay(1500)
        println("superVisorScopeWithContextType2: parent main builder")
    }

    launch {
        delay(500)
        println("superVisorScopeWithContextType2: coroutine builder")
    }
}
/*
supervisorScope does not support changing context. If you need to both change context and use a SupervisorJob,
you need to wrap supervisorScope with withContext.

Here, withContext(Dispatchers.IO) (line 51), just changed the context to io but running on same coroutine,
But later we created a separate scope(line 52) from parent, so any exception thrown in child of coroutine
will not be propagated to parent, exception will be silenced in direct child for supervisor scope child only(line 58)
* */


fun main(): Unit = runBlocking {
    //superVisorScopeWithContextType1()
    println()
    println()
    println()
    superVisorScopeWithContextType2()
}