package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SpaceColorScheme = darkColorScheme(
    primary = CosmicCyan,
    onPrimary = Color(0xFF0B1021),
    primaryContainer = Color(0xFF16423C),
    onPrimaryContainer = CosmicCyan,
    secondary = StarGold,
    onSecondary = Color(0xFF332000),
    secondaryContainer = Color(0xFF4A3800),
    onSecondaryContainer = StarGold,
    tertiary = NebulaViolet,
    onTertiary = Color.White,
    background = SpaceBackgroundDark,
    onBackground = TextWhite,
    surface = SpaceCardSurface,
    onSurface = TextWhite,
    surfaceVariant = SpaceBackgroundMedium,
    onSurfaceVariant = TextMuted,
    outline = SpaceCardBorder,
    error = LaserRose,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SpaceColorScheme,
        typography = Typography,
        content = content
    )
}
