import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.lang.Math.*
import java.util.Collections.rotate
import kotlin.math.pow

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun YoutubeButton(text: String) {
    var isHovered by remember { mutableStateOf(false) }
    // Animation copied from https://proandroiddev.com/animate-borders-in-jetpack-compose-ca359deed7d5
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val borderBrush = customBrush(listOf(
        Color(245, 0, 86),
        Color(245, 241, 0),
        Color(0, 175, 245)),
        angle
    )

    // Animate border width
    val borderWidth by animateIntAsState(
        targetValue = if (isHovered) 3 else 0,
        animationSpec = tween(durationMillis = 300))
    // When borderWidth is 0, then maybe don't set the border at all
    val border = if (borderWidth != 0) BorderStroke(borderWidth.dp, borderBrush) else null

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        border = border,
        shape = RoundedCornerShape(percent = 50),
        modifier = Modifier
            .onPointerEvent(eventType = PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(eventType = PointerEventType.Exit) { isHovered = false },
        onClick = { }) {
        Text(text)
    }
}

// Based on https://stackoverflow.com/questions/68218714/angled-gradient-background-in-jetpack-compose
// TODO: why the rotation doesn't look linear ?
// TODO: rewrite it with own logic
fun customBrush(colors: List<Color>, angle: Float) = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val angleRad = angle / 180f * PI
        val x = cos(angleRad).toFloat() //Fractional x
        val y = sin(angleRad).toFloat() //Fractional y

        val center = Offset(size.width / 2, size.height / 2)

        val radius = kotlin.math.sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = min(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
        )

        return LinearGradientShader(
            colors = colors,
            from = Offset(size.width, size.height) - exactOffset,
            to = exactOffset,
            tileMode = TileMode.Mirror
        )
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().background(color = Color(12, 12, 12)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                YoutubeButton("Subscribe")
            }
        }
    }
}
