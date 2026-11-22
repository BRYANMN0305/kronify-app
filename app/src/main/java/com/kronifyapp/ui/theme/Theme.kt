package com.kronifyapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/* Kronify es una app oscura por diseño: no sigue el tema del sistema
   ni el color dinámico. */
private val KronifyColorScheme = darkColorScheme(
    primary = Neon,
    onPrimary = Acero1,
    primaryContainer = NeonDim,
    onPrimaryContainer = Acero5,
    secondary = Amber,
    onSecondary = Acero1,
    background = Acero1,
    onBackground = TextPrimary,
    surface = SurfaceDeep,
    onSurface = TextPrimary,
    surfaceVariant = Acero2,
    onSurfaceVariant = TextSecondary,
    outline = StrokeStrong,
    outlineVariant = StrokeSubtle,
    error = DangerRed,
)

@Composable
fun KronifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KronifyColorScheme,
        typography = KronifyTypography,
        content = content,
    )
}
