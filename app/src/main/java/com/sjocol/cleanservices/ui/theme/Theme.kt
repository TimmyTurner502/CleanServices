package com.sjocol.cleanservices.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    secondary = BrandTeal,
    tertiary = BrandTeal,
    background = BackgroundDark,
    surface = BackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
)

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    secondary = BrandTeal,
    tertiary = BrandTeal,
    background = BackgroundLight,
    surface = BackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    outline = DividerLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun CleanServicesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Fijamos colores de marca
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}