package dev.reprator.composelearning.tutorials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun StabilityComparison() {
    var counter by remember { mutableStateOf(0) }

    val unstableLambda = {
        counter++
        println("RecompositionLambdaSampleList: click: Unstable lambda called with counter: ${counter.absoluteValue}")
    }

    val stableLambda = remember {
        {
            println("RecompositionLambdaSampleList: click: Stable lambda called ${counter.absoluteValue}")
        }
    }

    val partialStable = remember(counter) {
        {
            println("RecompositionLambdaSampleList: click: Lambda called with counter: $counter")
        }
    }

    println("RecompositionLambdaSampleList LambdaUnStableSample 1: unstableLambda:$unstableLambda, stableLambda:$stableLambda, partialStable:$partialStable")

    Column {
        println("RecompositionLambdaSampleList LambdaUnStableSample 2")
        Button(onClick = { counter++ }) {
            Text("Increment Counter: $counter")
        }

        RandomColorButton2(onClick = unstableLambda, text = "Unstable")
        RandomColorButton2(onClick = stableLambda, text = "Stable")
        RandomColorButton2(onClick = partialStable, text = "Partial Stable Captures Counter")
    }
}

/*
Output:
    Composition Logs:
            🔥 RecompositionLambdaSampleList Root
            🔥 RecompositionLambdaSampleList Root Container
            RecompositionLambdaSampleList LambdaUnStableSample 1: unstableLambda:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda2@eed0f3b, stableLambda:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda3@835a658, partialStable:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda4@796d3b1
            RecompositionLambdaSampleList LambdaUnStableSample 2
            RecompositionLambdaSampleList LambdaUnStableSample 3 Unstable
            RecompositionLambdaSampleList LambdaUnStableSample 4 Unstable
            RecompositionLambdaSampleList LambdaUnStableSample 3 Stable
            RecompositionLambdaSampleList LambdaUnStableSample 4 Stable
            RecompositionLambdaSampleList LambdaUnStableSample 3 Partial Stable Captures Counter
            RecompositionLambdaSampleList LambdaUnStableSample 4 Partial Stable Captures Counter

    Unstable button Click Logs:
            RecompositionLambdaSampleList: click: Unstable lambda called with counter: 1
            RecompositionLambdaSampleList LambdaUnStableSample 1: unstableLambda:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda2@eed0f3b, stableLambda:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda3@835a658, partialStable:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda4@f8eea49
            RecompositionLambdaSampleList LambdaUnStableSample 2
            RecompositionLambdaSampleList LambdaUnStableSample 3 Partial Stable Captures Counter
            RecompositionLambdaSampleList LambdaUnStableSample 4 Partial Stable Captures Counter

    Counter button Click Logs:
            RecompositionLambdaSampleList LambdaUnStableSample 1: unstableLambda:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda2@eed0f3b, stableLambda:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda3@835a658, partialStable:dev.reprator.composelearning.tutorials._4_1_recomposition_lambdaKt$$ExternalSyntheticLambda4@7c5a5ba
            RecompositionLambdaSampleList LambdaUnStableSample 2
            RecompositionLambdaSampleList LambdaUnStableSample 3 Partial Stable Captures Counter
            RecompositionLambdaSampleList LambdaUnStableSample 4 Partial Stable Captures Counter

 Explanation:
    On click of Unstable button:
            1) Since, counter++ is being incremented(line 25), it causes the recomposition to all the widget who ever is reading
               partialStable, Here
                    • partialStable(line 35) lambda is using counter as key for remember, it causes the recomposition of
                        "remember" and compose itself with newKey/updated value
                    • Since, RandomColorButton2(Line 51) is listening for "partialStable" it also recomposes

            2) Observation:
                    If you observe, 'unstableLambda(line 24)' reference is not getting changed, even without remember,
                    It survives the recomposition, it is because of
                             • It is declared in a stable way, where Compose sees no reactive input triggering its recreation.
                             • Even though it modifies state (counter++), Compose does not track that(unstableLambda lambda)
                                as an observable input.
                    This points prove our line 114, point.

    On click of Counter button:
            Same observation as on click of "Unstable button"
* */

@Composable
fun RecompositionLambdaSampleList(modifier: Modifier = Modifier) {
    println("🔥 RecompositionLambdaSampleList Root")

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
        .then(modifier)) {
        println("🔥 RecompositionLambdaSampleList Root Container")
        Spacer(Modifier.background(getRandomColor()))
        StabilityComparison()
    }
}

/*
Lambda Recomposition Rules:
    1) Compose automatically memoizes lambdas that don't capture anything or don't access changing state, Since kotlin 2.0 with
        strong strong stability mode.
                                The compiler sometimes optimizes such lambdas (i.e., keeps the same reference if it seems side-effect-only).
            1.a) How lambda works are as follows,
                📘 Compose Lambda Stability — Summary Notes
                    🧩 1. Lambdas in Kotlin = Objects
                            • Lambdas in Kotlin compile into objects implementing FunctionN (e.g., Function0, Function1).
                            • Two types:
                                    • Non-capturing lambda → no external reference
                                    • Capturing lambda → accesses outer variables

                    📦 2. How Kotlin Compiles Lambdas
                            ✅ Non-Capturing Lambda
                                        val lambda1 = { println("Hello") }
                                    Compiled as:
                                        object Lambda1 : Function<Unit> {
                                            override fun invoke() {
                                                println("Hello")
                                            }
                                        }
                                Summary:
                                    🧠 Singleton → reference never changes → Stable

                            ⚠️ Capturing Lambda
                                            val count = 4
                                            val lambda2 = { println("count: $count") }
                                    Compiled as:
                                            class Lambda2(private val count: Int) : Function<Unit> {
                                                override fun invoke() {
                                                    println("count: $count")
                                                }
                                            }
                                    If you capture unstable values like:
                                            • Context
                                            • ViewModel
                                            • Modifier
                                            • NavController
                                            • Unstable data class instances
                                            • list
                                        it will make the lambda as unstable(if it does not seem side-effect-only, line 118-119)

                                    Summary:
                                        🧠 Creates new instance if captured value changes → not singleton → Unstable

                            Reference:
                                    https://cheonjaeung.com/posts/jetpack-compose-recomposition-and-stability-system/

            1.b) If you don't want compiler to memoize it, automatically, you can annotate that with '@DontMemoize' like below,
                        val unstableLambda = @DontMemoize {
                                println("RecompositionLambdaSampleList: click: Unstable lambda called with counter: ${counter.absoluteValue}")
                        }
                    It will make lambda to be create new instance on every recomposition


    2) Steps to avoid, Capturing Lambda:

            2.a) Use references: It will prevent the creation of a new lambda that references the outside variable
                    Earlier:
                            @Composable
                            fun UnstableLambdaScreen() {
                                    val focusRequester = LocalFocusManager.current
                                    Column {
                                                // unstable lambda results in unskippable Button
                                                Button(onClick = { focusRequester.clearFocus() }) {
                                                    Text(text = "button with unstable lambda")
                                                }
                                            }
                                        }
                     After:
                            @Composable
                            fun UnstableLambdaScreen() {
                                    val focusRequester = LocalFocusManager.current
                                    Column {
                                                // Stable
                                                Button(onClick = viewModel::onContinueClick ) {
                                                    Text(text = "button with unstable lambda")
                                                }
                                            }
                                        }

            2.b) Remembering lambdas: It will ensure that we reuse the same lambda instance across recompositions.
                    Example:
                            • partialStable(line 35)
                            • @Composable
                              fun UnstableLambdaScreen(viewModel: TypicalViewModel = hiltViewModel()) {
                                val focusRequester = LocalFocusManager.current
                                val onNameEnteredClick: (value: String) -> Unit = remember {
                                        return@remember viewModel::onNameEntered
                                }
                                val clearFocus = remember { { focusRequester.clearFocus() } }

                                Column {
                                    // stable lambdas, skippable button
                                    Button(onClick = {
                                    onNameEnteredClick("..")
                                    clearFocus()
                                    someTopLvlFunction()
                                    }) {
                                            Text(text = "button with stable lambdas")
                                        }
                                    }
                                }

            2.c) Avoid reading unstable data inside lambdas:
                    Avoid non stable types like Context, ViewModel, Modifier, NavController, Unstable data class
                     instances, list etc inside lambda

Terminology:
    • Unstable Lambda: It is a lambda expression that is recreated (a new instance is created) during every recomposition of
                       its parent composable(line 24).
                       Example:
                            • partialStable(line 35) is an partial stable lambda, as remember is dependent on 'counter'.
                                So, any partial stable lambda is also called as unstable lambda
                            • unstableLambda(line 25) is also an unstable lambda but since, it follows the rule 1(line 118),
                                it will be memoized and hence will not be recreated


    • Stable Lambda: It is a lambda which is reused across recompositions and does not change.
                     Lambdas wrapped in remember retain the same reference across recompositions
                     Example: stableLambda(line 29) is an example of stable lambda

* */