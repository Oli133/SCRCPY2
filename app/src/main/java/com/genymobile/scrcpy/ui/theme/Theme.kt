package com.genymobile.scrcpy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ScrcpyTeal = Color(0xFF087164)
private val ScrcpyEmerald = Color(0xFF34DC83)
private val ScrcpyDarkGreen = Color(0xFF0C3B34)
private val ScrcpyMintLight = Color(0xFF7AE7A7)

private val DarkColorScheme = darkColorScheme(
    primary = ScrcpyEmerald,
    onPrimary = Color(0xFF003823),
    primaryContainer = ScrcpyDarkGreen,
    onPrimaryContainer = ScrcpyMintLight,
    secondary = Color(0xFF4DD0E1),
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFFB2EBF2),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF4A2800),
    background = Color(0xFF0C1412),
    onBackground = Color(0xFFE1E8E5),
    surface = Color(0xFF121B19),
    onSurface = Color(0xFFE1E8E5),
    surfaceVariant = Color(0xFF1D2926),
    onSurfaceVariant = Color(0xFFB5C4BF),
    outline = Color(0xFF364843)
)

private val LightColorScheme = lightColorScheme(
    primary = ScrcpyTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F5DE),
    onPrimaryContainer = Color(0xFF002115),
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00201B),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = Color(0xFFF7FAF8),
    onBackground = Color(0xFF131917),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF131917),
    surfaceVariant = Color(0xFFE3EDE9),
    onSurfaceVariant = Color(0xFF3F4D48),
    outline = Color(0xFF8BA099)
)

@Composable
fun ScrcpyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
        content = content
    )
}
