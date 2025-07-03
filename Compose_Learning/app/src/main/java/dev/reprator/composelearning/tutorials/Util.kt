package dev.reprator.composelearning.tutorials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

fun getRandomColor() =  Color(
    red = Random.nextInt(256),
    green = Random.nextInt(256),
    blue = Random.nextInt(256),
    alpha = 255
)


@Composable
fun RandomColorColumn(content: @Composable () -> Unit) {

    println("📌 RandomColumn() COMPOSABLE: $content")
    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(1.dp, shape = CutCornerShape(topEnd = 8.dp))
            .background(getRandomColor())
            .padding(4.dp)
    ) {
        println("📌📌 RandomColumn() SCOPE")
        content()
        println("📌📌 RandomColumn() SCOPE AFTER")
    }
}

@Composable
fun RandomColorButton(onClick: () -> Unit, content: @Composable () -> Unit) {

    println("💬 RandomColorButton COMPOSABLE: $content")

    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = getRandomColor()),
        onClick = onClick,
        shape = RoundedCornerShape(5.dp)
    ) {
        println("💬💬 RandomColorButton() SCOPE")
        content()
    }
}