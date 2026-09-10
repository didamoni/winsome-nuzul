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
    primary = ImperialEvergreen,
    onPrimary = PureSilkWhite,
    primaryContainer = SoftTravertine,
    onPrimaryContainer = ImperialEvergreen,

    secondary = OliveDriftwood,
    onSecondary = PureSilkWhite,
    secondaryContainer = SoftTravertine,
    onSecondaryContainer = ObsidianCharcoal,

    tertiary = BurnishedGold,
    onTertiary = PureSilkWhite,
    tertiaryContainer = SoftTravertine,
    onTertiaryContainer = ObsidianCharcoal,

    background = WarmAlabaster,
    onBackground = ObsidianCharcoal,

    surface = PureSilkWhite,
    onSurface = ObsidianCharcoal,
    surfaceVariant = SoftTravertine,
    onSurfaceVariant = MutedSlateStone,

    outline = FineLinenBorder,
    outlineVariant = FineLinenBorder.copy(alpha = 0.6f)
)

private val DarkColorScheme = darkColorScheme(
    primary = RadiantGold,
    onPrimary = DarkOilNoir,
    primaryContainer = ElevatedCanopy,
    onPrimaryContainer = RadiantGold,

    secondary = VividMineralJade,
    onSecondary = DarkOilNoir,
    secondaryContainer = ElevatedCanopy,
    onSecondaryContainer = WarmIvoryText,

    tertiary = RadiantGold,
    onTertiary = DarkOilNoir,

    background = DarkOilNoir,
    onBackground = WarmIvoryText,

    surface = DeepOliveForest,
    onSurface = WarmIvoryText,
    surfaceVariant = ElevatedCanopy,
    onSurfaceVariant = PaleOliveMuted,

    outline = DarkOliveBorder,
    outlineVariant = DarkOliveBorder.copy(alpha = 0.7f)
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