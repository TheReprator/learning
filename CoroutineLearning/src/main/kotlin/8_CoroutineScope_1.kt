package org.example

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CustomPresenter : CoroutineScope {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->

    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob() + exceptionHandler

    private val scope = CoroutineScope(coroutineContext)

    fun onCleared() {
        //scope.cancel()
        //scope.coroutineContext.cancelChildren() or
        coroutineContext.cancelChildren()
    }
}

/*
* Here we are create our Own CoroutineScope like viewmodel(whose viewmodelscope is like internally,
* CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) )
*
* First we extend the class with CoroutineScope(line 8), then we override the only property coroutineContext(line 12)
* Since, Dispatchers.Main is considered the best option as the default dispatcher for android,
*
*
* Even better, it is a common practice to not cancel the whole scope but only its children(line 18 and 19 are equivalent).
* Thanks to that, as long as this view model is active, new coroutines can start on its scope property.
*
* We also want different coroutines started on this scope to be independent. When we use Job, if any of the children is
* cancelled due to an error, the parent and all its other children are cancelled as well. So to have that independence,
* we should use SupervisorJob(line 12) instead of Job.
*
* The last important functionality is the default way of handling un-aught exceptions. On Android, we often define what
* should happen in the case of, different kinds of exceptions. So we had added CoroutineExceptionHandler(line 8) and
* made it as part of context (line 12), so that it can catch any uncaught exception
*
* Like we had discussed in '5_ExceptionHandling_3_supervisorJob.kt type1()', any exception that is raised normally or inside
* supervisorJob, is uncaught exception(fatal) in android, So it needs to be caught in android(using CoroutineExceptionHandler)
* else app will crash, whereas in case of plain kotlin, uncaught coroutine exceptions are printed to stderr(which are
* non-fatal in nature),hence does not crash, with supervisorJob or supervisorScope,
*
* */
