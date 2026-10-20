package com.maroondevelopment.networth.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Colours the app needs that Material 3 has no role for.
 *
 * Gain and loss are brand meaning rather than surface hierarchy, so they live here and get their
 * own value per theme: the dark variants are lightened to stay legible on a dark background.
 */
@Immutable
data class AppColors(
    val gain: Color,
    val loss: Color
)

private val LightAppColors = AppColors(
    gain = Color(0xFF1B873F),
    loss = Color(0xFFC62828)
)

private val DarkAppColors = AppColors(
    gain = Color(0xFF4ADE80),
    loss = Color(0xFFF87171)
)

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFFCFCFC),
    surface = Color(0xFFFCFCFC),
    surfaceVariant = Color(0xFFF2F2F2),
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = Color(0xFF55595E)
)

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF111315),
    surface = Color(0xFF111315),
    surfaceVariant = Color(0xFF1F2225),
    onSurface = Color(0xFFE6E7E9),
    onSurfaceVariant = Color(0xFFA8ACB1)
)

private val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/** The app's gain / loss colours for the theme in effect. */
val appColors: AppColors
    @Composable @ReadOnlyComposable get() = LocalAppColors.current

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalAppColors provides if (useDarkTheme) DarkAppColors else LightAppColors
    ) {

        MaterialTheme(
            colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
        ) {

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }

        }

    }
}
