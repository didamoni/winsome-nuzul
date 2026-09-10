package com.winsome.nuzul.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DeepEvergreen,
    onPrimary = PureWhite,
    primaryContainer = SoftSand,
    onPrimaryContainer = DeepEvergreen,

    secondary = Forest,
    onSecondary = PureWhite,
    secondaryContainer = SoftSand,
    onSecondaryContainer = Charcoal,

    tertiary = Champagne,
    onTertiary = DeepEvergreen,
    tertiaryContainer = SoftSand,
    onTertiaryContainer = Charcoal,

    background = WarmIvory,
    onBackground = Charcoal,

    surface = PureWhite,
    onSurface = Charcoal,
    surfaceVariant = SoftSand,
    onSurfaceVariant = MutedGray,

    outline = DividerColor,
    outlineVariant = DividerColor.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = SeaGlass,
    onPrimary = DeepEvergreen,
    primaryContainer = Forest,
    onPrimaryContainer = WarmIvory,

    secondary = Champagne,
    onSecondary = DeepEvergreen,
    secondaryContainer = Forest,
    onSecondaryContainer = WarmIvory,

    tertiary = Champagne,
    onTertiary = DeepEvergreen,

    background = DeepEvergreen,
    onBackground = WarmIvory,

    surface = Forest,
    onSurface = WarmIvory,
    surfaceVariant = DeepEvergreen.copy(alpha = 0.7f),
    onSurfaceVariant = SeaGlass,

    outline = Forest.copy(alpha = 0.7f),
    outlineVariant = Forest
)

@Composable
fun NuzulTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NuzulTypography,
        shapes = NuzulShapes,
        content = content
    )
}