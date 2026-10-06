package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SpaceBackgroundDark
import com.example.ui.theme.SpaceBackgroundMedium
import kotlin.random.Random

data class StarDot(val xRatio: Float, val yRatio: Float, val radius: Float, val alpha: Float)

@Composable
fun CosmicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Generate a fixed set of star coordinates for consistent visual appeal
    val stars = remember {
        val rand = Random(42)
        List(70) {
            StarDot(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                radius = rand.nextFloat() * 2.2f + 0.8f,
                alpha = rand.nextFloat() * 0.7f + 0.3f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SpaceBackgroundDark,
                        SpaceBackgroundMedium,
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Subtle nebula glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1F00E5BC), Color.Transparent),
                    center = Offset(width * 0.85f, height * 0.15f),
                    radius = width * 0.6f
                ),
                center = Offset(width * 0.85f, height * 0.15f),
                radius = width * 0.6f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x189B51E0), Color.Transparent),
                    center = Offset(width * 0.15f, height * 0.75f),
                    radius = width * 0.7f
                ),
                center = Offset(width * 0.15f, height * 0.75f),
                radius = width * 0.7f
            )

            // Draw twinkling stars
            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = star.alpha),
                    radius = star.radius,
                    center = Offset(star.xRatio * width, star.yRatio * height)
                )
            }
        }

        content()
    }
}
