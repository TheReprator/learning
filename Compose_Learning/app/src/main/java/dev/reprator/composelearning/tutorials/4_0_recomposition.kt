package dev.reprator.composelearning.tutorials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
private fun RecomposableInlineSample() {
    var counter by remember { mutableIntStateOf(0) }
    var counter2 by remember { mutableIntStateOf(0) }

    println("Root RecomposableSample")

    Column {
        println("Inside Column")

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                counter++
                println("🔥 Button counter click")
            }
        ) {
            println("Button Scope 1")
            Text(text = "Counter: $counter")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                counter2++
                println("🔥 Button counter2 click")
            }
        ) {
            println("Button Scope 2")
            Text(text = "Counter2: $counter2")
        }

        Text(text = "Counter: $counter")
        //CounterText(counter)
    }
}

@Composable
private fun CounterText(counter: Int) {
    Text(text = "Counter: $counter")
}
/*
Here, onclick of Button 2:
        🔥 Button counter2 click
        Button Scope 2

      Explanation:
            It can be divided into 3 steps:
                a) On button2 click, only button lambda gets invalidated, due to counter2 update
                b) As counter2 state is being read only inside text composable of button2,
                c) So it finds the nearest non-inline recomposable scope, which is button(Button in non-line composable)
                   And hence, only button lambda(line 47-50) gets recomposed

Here, onclick of Button 1:
        🔥 Button counter click
        Root RecomposableSample
        Inside Column
        Button Scope
        Button Scope2

     Explanation:
            a) On button1 click, only button lambda gets invalidated, due to counter1 update
            b) As counter1 state is being read, inside text(line 38) composable of button1 and Text(line 52)
            c) For button1 text, the nearest non-inline composable scope is button lambda(line 36-39)
               but for Text(line 52), the nearest non-inline recomposable scope is the whole composable function
            d) Because, Text(line 52) resides in Column(line 27) which is inline composable, and column(line 27)
               lives inside composable function which is non inline, so therefore, parent composable or function
               become its nearest recomposable scope.
                            That's the reason whole body is recomposed

        Note:
            e) Even if you had moved Text(line 52) into separate composable like line 58(comment line 52,
                uncomment ine 53), still the output would be same, as text is a composable function callee
                function, whose direct parent is Column(line 27), which is inline.

            f) If you add, inline in front of function at line 21 like below,
                            private inline fun RecomposableInlineSample() {}
                        Then output will be like below, on click of button 1:
                                🔥 Button counter click
                                Root
                                Root container
                                Root RecomposableSample
                                Inside Column
                                Button Scope 1
                                Button Scope 2
                     It will happen because, in that case the nearest non-inline recomposable scope is of
                     parent container(RecomposableSampleList), So the whole body of "RecomposableSampleList"
                     will be recomposed.
 */

@Composable
private fun RecomposableLambdaSample1() {
    var counter by remember { mutableIntStateOf(0) }

    println("Root RecomposableInlineSample1")

    RandomColorColumn {
        println("RecomposableInlineSample1: Inside Column")

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                counter++
                println("RecomposableInlineSample1: 🔥 Button counter click")
            }
        ) {
            println("RecomposableInlineSample1: Button Scope 1")
            Text(text = "Counter: $counter")
        }

        Text(text = "Counter: $counter")
    }
}

/*
This example of button1 is same as in RecomposableInlineSample

Output:
    RecomposableInlineSample1: 🔥 Button counter click
    RecomposableInlineSample1: Inside Column
    RecomposableInlineSample1: Button Scope 1

Explanation:
            a) On button click, only button lambda gets invalidated, due to counter update
            b) As counter state is being read, inside text(line 127) composable of button and Text(line 130)
            c) For button text, the nearest non-inline composable scope is button lambda(line 125-128)
               but for Text(line 130), the nearest non-inline recomposable scope is the RandomColorColumn(line 116)
                            So therefore, RandomColorColumn(line 116) become its nearest recomposable scope.
      Note:
            a) In RecomposableInlineSample, onclick of button1, the whole function is recomposed, because
                Column was inline but here only lambda of RandomColorColumn(line 116) is recomposed, as
                    RandomColorColumn is a non-inline composable function, which host another composable function
                    in itself.
            b) Following logs were not printed from RandomColorColumn, on click of button
                    📌📌 RandomColumn() SCOPE
                    📌📌 RandomColumn() SCOPE AFTER
                 which proves only the current lambda content will be recomposed.
* */

@Composable
fun RecomposableSampleList(modifier: Modifier = Modifier) {
    println("Root")
    Column(modifier.verticalScroll(rememberScrollState())) {
        println("Root container")
        Text(text = "Inline Sample")
        Spacer(Modifier.background(getRandomColor()).height(30.dp))
        RecomposableInlineSample()
        Spacer(Modifier.background(getRandomColor()).height(30.dp))
        RecomposableLambdaSample1()
    }
}

/*
Recomposition: It is the process of calling your composable functions again when inputs change. This happens
               when the function's inputs change. When Compose recomposes based on new inputs, it only calls the
               functions or lambdas that might have changed, and skips the rest. By skipping all functions or
               lambdas that don't have changed parameters, Compose can recompose efficiently.

        1) Recomposition happens in closest non-inline scope that reads any State change. A scope is non-inline
           @Composable function that returns Unit
        2) Rule of thumb:
                 If any state is read in a Composable scope, then that entire scope till non-inline is subject to
                 recomposition when that state changes.
        3) If the composable are passed as lambda, then it will not recompose, as they are considered as stable
        4) Reading unstable types inside lambda causes function to be recomposed.

Reference:
    (Recomposition)https://www.jetpackcompose.app/articles/donut-hole-skipping-in-jetpack-compose?ref=blog.zachklipp.com
    https://medium.com/androiddevelopers/jetpack-compose-stability-explained-79c10db270c8
    https://chrisbanes.me/posts/composable-metrics/
* */