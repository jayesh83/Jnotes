package com.jayesh.jnotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.Transparent,
    surface = Grey900,
    onSurface = Grey300
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.Transparent,
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun JnotesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun JnotesTheme(
    backgroundColor: Color,
    contentColor: Color,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        val reverseTheDefault = backgroundColor == WhiteMutated && contentColor == BlackMuted
        val newBackgroundColor = if (reverseTheDefault) BlackMuted else backgroundColor
        val newContentColor = if (reverseTheDefault) WhiteMutated else contentColor

        DarkColorPalette.copy(background = newBackgroundColor, onBackground = newContentColor)
    } else {
        LightColorPalette.copy(background = backgroundColor, onBackground = contentColor)
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}