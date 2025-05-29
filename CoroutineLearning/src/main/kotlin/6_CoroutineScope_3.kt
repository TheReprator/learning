package org.example

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

private interface ICallback{
    fun show(user: User, fromCaller: String)
}

private data class User(val name: String, val friends: String, val profile: String)

private class ActivityUserUseCase: ICallback {
    override fun show(user: User, fromCaller: String) {
        println("6_CoroutineScope_3:: ActivityUserUseCase: fromCaller: $fromCaller \n $user")
    }
}

private class ShowUserDataUseCase(private val iCallback: ICallback) {
    suspend fun showUserData() = coroutineScope {
        val name = async {
            delay(1000L)
            "vikram"
        }
        val friends = async {
            delay(500L)
            "family"
        }

        val profile = async {
            delay(1500L)
            "github"
            //throw Exception("data call fail")
        }

        val user = User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
        iCallback.show(user, "ShowUserDataUseCase")
        launch {
            delay(1000L)
            println("6_CoroutineScope_3: ShowUserDataUseCase: analytics operation after callback")
         //  throw Exception("For testing dummy exception throw")
        }
    }
}

private suspend fun type1() {
    val activityUserUseCase = ActivityUserUseCase()
    val showUserDataUseCase = ShowUserDataUseCase(activityUserUseCase)
    showUserDataUseCase.showUserData()
}
/*
  Issues:
* Firstly, this launch(line 41) does nothing here because coroutineScope needs to await its completion anyway.
* So if you are showing a progress bar or some other operation before and after line 51, the user needs to wait
* until this launch builder is finished as well, due to its boundness in coroutine scope(line 18),
* It delays the user interaction by 1 second for end user to start interaction, which is waste, as
* analytics could have done in separate scope, due to non-importance nature of end user
*
*
* Second issue(cancellation): Coroutines are designed (by default) to cancel other operations when there is an exception.
* This is great for essential operations like If profile(uncomment line 32) has an exception, we should cancel name and
* friends because their response would be useless anyway. However, canceling a process(line 18) just because an
* analytics call(uncomment line 44) has failed does not make much sense. As analytics is never a priority for end user
*
* */


private class ShowUserDataUseCase2(private val iCallback: ICallback,
                                   private val analyticsScope: CoroutineScope) {
    suspend fun showUserData() = coroutineScope {
        val name = async {
            delay(1000L)
            "vikram"
        }
        val friends = async {
            delay(500L)
            "family"
        }

        val profile = async {
            delay(1500L)
            "github"
        }

        val user = User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
        iCallback.show(user, "ShowUserDataUseCase2")

        analyticsScope.launch {
            delay(1000L)
            println("6_CoroutineScope_3: ShowUserDataUseCase2: analyticsScope: analytics operation after callback1")
            //throw Exception("For testing duumy exception throw")
        }
        analyticsScope.launch {
            delay(800L)
            println("6_CoroutineScope_3: ShowUserDataUseCase2: analyticsScope: analytics operation after callback2")
        }

        analyticsScope.launch {
            delay(3000L)
            println("6_CoroutineScope_3: ShowUserDataUseCase2: analyticsScope: analytics operation after callback3")
        }

        launch {
            delay(500L)
            println("6_CoroutineScope_3: ShowUserDataUseCase2: dummy builders 1")
        }
    }
}

private suspend fun type2() {
    val activityUserUseCase = ActivityUserUseCase()
    val analyticsScope = CoroutineScope(SupervisorJob())

    val showUserDataUseCase2 = ShowUserDataUseCase2(activityUserUseCase, analyticsScope)
    val timeTaken = measureTimeMillis {
        showUserDataUseCase2.showUserData()
    }
    println("time taken: $timeTaken")
    delay(3000L)
}

/*
* Passing a scope(line 72) clearly signals that such a class can start independent calls(line 95,100,105).
* This means suspending functions might not wait for all the operations they start(line 95,100,105). As they are
* launched in separate scope.
*
* This fact is proved by timeTake(line 125), which gives output as 2032 milliseconds.
* And if calculate the time taken by function(showUserDataUseCase2.showUserData() line 123), it should be minimum
* 3 seconds(line 106). But since builders(95,100,105) are launched in separate scope, function showUserData(line 73)
* does not wait for their completion.
*
* Even if we had thrown an exception(line 98), it's is not going to hurt function showUserData(line 73), as they belong
* to separate scope.
*
* One more thing, if you want to see the full output of function type2(line 117), you have to delay it by a
* minimum of 3 seconds(line 126), as analyticsScope(line 116) is independent job, which breaks the parent child relationship
* and it takes max 3 seconds(line 106) in one of its builders
* */
fun main(): Unit = runBlocking {
    type1()
    println()
    println()
    println()
    type2()
}