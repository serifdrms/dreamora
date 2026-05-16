package com.isteserif.dreamora.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val DreamColorScheme = darkColorScheme(
    primary = LightPurple,
    onPrimary = DeepPurple,
    primaryContainer = DarkPurple,
    onPrimaryContainer = PalePurple,
    secondary = MutedPurple,
    onSecondary = DeepPurple,
    secondaryContainer = MidPurple,
    onSecondaryContainer = PalePurple,
    background = DeepPurple,
    onBackground = PalePurple,
    surface = SurfacePurple,
    onSurface = PalePurple,
    surfaceVariant = MidPurple,
    onSurfaceVariant = MutedPurple,
    error = Color(0xFFCC4477),
    onError = DeepPurple,
)

@Composable
fun DreamoraTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkPurple.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = DreamColorScheme,
        typography = Typography,
        content = content
    )
}