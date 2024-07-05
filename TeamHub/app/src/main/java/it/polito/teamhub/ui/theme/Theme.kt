package it.polito.teamhub.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PurpleBlue,
    onPrimary = Color.White,
    secondary = PurpleBlue,
    onSecondary = Color.White,
    tertiary = Gray3,
    onTertiary = Color.White,
    background = GraySurface,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Gray3,
    error = RedOrange,
    onError = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleBlue,
    onPrimary = Color.White,
    secondary = RoyalBlue,
    onSecondary = Color.White,
    tertiary = Gray4,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Gray5,
    error = RedOrange,
    onError = Color.White,
)

@Composable
fun TeamHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}