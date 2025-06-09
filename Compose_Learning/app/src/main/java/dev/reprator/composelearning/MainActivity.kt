package dev.reprator.composelearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import dev.reprator.composelearning.ui.theme.ComposeLearningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLearningTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    /*Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
                    MySubcomposeLayout( modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MySubcomposeLayout(modifier: Modifier = Modifier) {
    SubcomposeLayout { constraints ->

        val topMeasurables = subcompose("top") {
            Text("I'm at the top")
        }

        val topPlaceables = topMeasurables.map { it.measure(constraints) }
        val topHeight = topPlaceables.maxOf { it.height }

        val bottomMeasurables = subcompose("bottom") {
            Text("I'm below top")
        }
        val bottomPlaceables = bottomMeasurables.map { it.measure(constraints) }

        val totalHeight = topHeight + bottomPlaceables.maxOf { it.height }+220

        layout(constraints.maxWidth, totalHeight) {
            var y = 220
            topPlaceables.forEach {
                it.placeRelative(0, y)
                y += it.height
            }

            bottomPlaceables.forEach {
                it.placeRelative(0, y)
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeLearningTheme {
        Greeting("Android")
    }
}