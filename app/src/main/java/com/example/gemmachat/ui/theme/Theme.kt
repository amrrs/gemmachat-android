package com.example.gemmachat.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = AccentPurple,
    onPrimary = TextPrimary,
    primaryContainer = AccentPurple.copy(alpha = 0.2f),
    onPrimaryContainer = TextPrimary,
    secondary = AccentViolet,
    onSecondary = TextPrimary,
    background = BgDark,
    onBackground = TextPrimary,
    surface = BgCard,
    onSurface = TextPrimary,
    surfaceVariant = BgMid,
    onSurfaceVariant = TextSecondary,
    outline = Divider,
    error = ErrorRed,
)

@Composable
fun GemmaChatTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgDark.toArgb()
            window.navigationBarColor = BgDark.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content,
    )
}
