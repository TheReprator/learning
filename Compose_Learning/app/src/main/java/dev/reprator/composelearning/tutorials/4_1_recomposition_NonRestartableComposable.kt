package dev.reprator.composelearning.tutorials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@OptIn(ExperimentalComposeApi::class)
@NonRestartableComposable
@Composable
fun RecompositionNonRestartableAnnotationSample_static() {
    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 3: RecompositionNonRestartableAnnotationSample_static")
        Text("3: RecompositionNonRestartableAnnotationSample_static")
    }
}

@OptIn(ExperimentalComposeApi::class)
@Composable
@NonRestartableComposable
fun RecompositionNonRestartableAnnotationSample_parameter(int: Int) {
    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 4: RecompositionNonRestartableAnnotationSample_parameter")
        Text("4: RecompositionNonRestartableAnnotationSample_parameter")
    }
}

@OptIn(ExperimentalComposeApi::class)
@Composable
@NonRestartableComposable
fun RecompositionNonRestartableAnnotationSample_parameter_log(int: Int) {
    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 4.1: RecompositionNonRestartableAnnotationSample_parameter $int")
        Text("4: RecompositionNonRestartableAnnotationSample_parameter")
    }
}

@OptIn(ExperimentalComposeApi::class)
@NonRestartableComposable
@Composable
fun RecompositionNonRestartableAnnotationSample_parameter_read(int: Int) {
    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 5: RecompositionNonRestartableAnnotationSample_parameter_read")
        Text("5 RecompositionNonRestartableAnnotationSample_parameter_read $int")
    }
}


@OptIn(ExperimentalComposeApi::class)
@Composable
@NonRestartableComposable
fun RecompositionNonRestartableAnnotationSample_parameter_button(int: Int) {
    println("RecompositionSample NonRestartableComposableSample recomposing: 6: RecompositionNonRestartableAnnotationSample_parameter_button")
    var counter by remember { mutableStateOf(0) }
    Button(onClick = {counter++}) {
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:0: $counter, value: $int")
    }
    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 6.1: RecompositionNonRestartableAnnotationSample_parameter_button")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:1:  counter: $counter, value: $int")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:2:  counter: $counter")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:3:  value: $int")
    }

    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 6.2: RecompositionNonRestartableAnnotationSample_parameter_button")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:1:1  counter: $counter, value: $int")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:2:1  counter: $counter")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:3:1  value: $int")
    }

    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 6.3: RecompositionNonRestartableAnnotationSample_parameter_button")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:2:2  counter: $counter")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:3:2  value: $int")
    }

    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 6.4: RecompositionNonRestartableAnnotationSample_parameter_button")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button:3:3  value: $int")
    }

    RandomColorColumn {
        println("RecompositionSample NonRestartableComposableSample recomposing: 6.5: RecompositionNonRestartableAnnotationSample_parameter_button")
        Text("6: RecompositionNonRestartableAnnotationSample_parameter_button  normal")
    }
}

@Composable
fun RecompositionNonRestartableAnnotationSample() {
    var counter by remember { mutableStateOf(0) }

    println("RecompositionSample NonRestartableComposableSample recomposing: 1: inside root")
    Column {
        println("RecompositionSample NonRestartableComposableSample recomposing: 2: inside column")
        Button(onClick = { counter++ }) {
            Text("NonRestartable sample countainer Count: $counter")
        }

        RecompositionNonRestartableAnnotationSample_static()  // Will NOT recompose
        RecompositionNonRestartableAnnotationSample_parameter(counter)  // Will NOT recompose
        RecompositionNonRestartableAnnotationSample_parameter_log(counter)  // Will NOT recompose
        RecompositionNonRestartableAnnotationSample_parameter_read(counter)  // Will NOT recompose
        RecompositionNonRestartableAnnotationSample_parameter_button(counter)  // Will NOT recompose
    }
}

/*
Output Logs:
    1) With NonRestartableComposable:
        on click of button(NonRestartable sample countainer Count, line 113):
            RecompositionSample NonRestartableComposableSample recomposing: 1: inside root
            RecompositionSample NonRestartableComposableSample recomposing: 2: inside column
            RecompositionSample NonRestartableComposableSample recomposing: 4.1: RecompositionNonRestartableAnnotationSample_parameter 1
            RecompositionSample NonRestartableComposableSample recomposing: 5: RecompositionNonRestartableAnnotationSample_parameter_read
            RecompositionSample NonRestartableComposableSample recomposing: 6: RecompositionNonRestartableAnnotationSample_parameter_button
            RecompositionSample NonRestartableComposableSample recomposing: 6.1: RecompositionNonRestartableAnnotationSample_parameter_button
            RecompositionSample NonRestartableComposableSample recomposing: 6.2: RecompositionNonRestartableAnnotationSample_parameter_button
            RecompositionSample NonRestartableComposableSample recomposing: 6.3: RecompositionNonRestartableAnnotationSample_parameter_button
            RecompositionSample NonRestartableComposableSample recomposing: 6.4: RecompositionNonRestartableAnnotationSample_parameter_button

        on click of button(6: RecompositionNonRestartableAnnotationSample_parameter_button, line 72):
            RecompositionSample NonRestartableComposableSample recomposing: 6.1: RecompositionNonRestartableAnnotationSample_parameter_button
            RecompositionSample NonRestartableComposableSample recomposing: 6.2: RecompositionNonRestartableAnnotationSample_parameter_button
            RecompositionSample NonRestartableComposableSample recomposing: 6.3: RecompositionNonRestartableAnnotationSample_parameter_button

    2) If we remove, all "@NonRestartableComposable" from function:
            Even then, all the logs will remain same, on click of respective button
* */

@OptIn(ExperimentalComposeApi::class)
@NonRestartableComposable
@Composable
fun RecompositionWithNonRestartableComposableSample() {
    println("🔥 RecompositionLambdaSampleList: Recomposition_Normal_vs_NonRestartableComposableSample: 3: RecompositionWithNonRestartableComposableSample")
    Text("RecompositionWithNonRestartableComposableSample")
}


@Composable
fun RecompositionWithoutNonRestartableComposableSample() {
    println("🔥 RecompositionLambdaSampleList: Recomposition_Normal_vs_NonRestartableComposableSample: 4: RecompositionWithoutNonRestartableComposableSample")
    Text("RecompositionWithoutNonRestartableComposableSample")
}

@Composable
fun Recomposition_Normal_vs_NonRestartableComposableSample() {

    println("🔥 RecompositionLambdaSampleList: Recomposition_Normal_vs_NonRestartableComposableSample: 1: Root")

    var counter by remember { mutableStateOf(0) }

    Column {
        println("🔥 RecompositionLambdaSampleList: Recomposition_Normal_vs_NonRestartableComposableSample: 2: Column")

        Button(onClick = { counter++ }) {
            Text("Recomposition_Normal_vs_NonRestartableComposableSample Count: $counter")
        }

        RecompositionWithNonRestartableComposableSample()
        RecompositionWithoutNonRestartableComposableSample()
    }
}

/*
Output :
    on click of button("Recomposition_Normal_vs_NonRestartableComposableSample Count"):
            Nothing happens:

Explanation:
    1) This proves that even without NonRestartableComposable annotation, composable will behave
       same as normal composable, if state is not being read normally.
                As RecompositionWithNonRestartableComposableSample & RecompositionWithoutNonRestartableComposableSample
       both are not reading anything parent. So, compose compiler behave normally

    2) 🤔 Why Use @NonRestartableComposable if Compose Already Skips Recomposing?,
            2.a) So we might be asking:
                “If a composable doesn't read any state, Compose skips recomposition anyway.
                So what's the point of marking @NonRestartableComposable?”

            2.b)
                ✅ Here's the Core Idea:

                ⚙️ Without @NonRestartableComposable:
                    • Compose checks if the function body should re-run during recomposition.
                    • If no state is read, Compose skips running it — smart optimization.
                    • But this check still happens at runtime (based on slot table and dirty flags).

                🧱 With @NonRestartableComposable:
                    • You’re telling the compiler and runtime up-front:
                        🔒 “This function will never need recomposition. Don’t even track it.”
                    • The Compose compiler then optimizes it harder:
                        • It omits tracking dirty flags.
                        • It removes observation setup altogether.
                        • It's guaranteed non-restartable, even if Compose internals change later.
                        • Can help eliminate unnecessary lambdas or internal remember checks.

               2.c)
                📌 Analogy
                    • Without @NonRestartableComposable, Compose is like a security guard who checks
                        every time but usually lets you through.
                    • With @NonRestartableComposable, it’s like having a VIP pass — the guard doesn’t
                        even check anymore.

               2.d)
                🧪 Conclusion
                    • You don’t need it for correctness if your composable isn’t reading state.
                    • But it’s **valuable for:
                        • Performance tuning** in deeply nested or large recomposing trees.
                        • Making sure some content never recomposes, even by accident.
                        • Giving compile-time guarantees to Compose internals.

    @NonRestartableComposableApplying to a Composable function tells the runtime to update its parameters
        without restarting the function, allowing it to maintain internal state and ongoing side effects.

 Reference:
    https://www.youtube.com/watch?v=h1xTtTl0k7Q&ab_channel=KotlinbyJetBrains
    https://velog.io/@skydoves/compose-stability
* */

@Composable
fun RecompositionNonRestartableComposableSample(modifier: Modifier = Modifier) {
    println("🔥 RecompositionSample Root")

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()).then(modifier)) {
        println("🔥 RecompositionSample Root Column Container")
        Text(modifier = Modifier.padding(top = 30.dp, bottom = 20.dp), text = "Recomposition_Normal_vs_NonRestartableComposableSample ")
        Recomposition_Normal_vs_NonRestartableComposableSample()

        HorizontalDivider(thickness = 50.dp, color = Color.Red, modifier = Modifier
            .background(getRandomColor())
            .height(30.dp))

        Text(modifier = Modifier.padding(top = 30.dp), text = "NonRestartableAnnotationSample ")
        RecompositionNonRestartableAnnotationSample()
    }
}