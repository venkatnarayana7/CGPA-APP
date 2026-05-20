package com.gradeflow.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight, onPrimary = OnPrimaryLight,
    primaryContainer = SecondaryLight, onPrimaryContainer = OnSecondaryLight,
    secondary = SecondaryLight, onSecondary = OnSecondaryLight,
    tertiary = TertiaryLight, onTertiary = OnPrimaryLight,
    background = BackgroundLight, onBackground = OnBackgroundLight,
    surface = SurfaceLight, onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight, onSurfaceVariant = OnSurfaceLight,
    error = ErrorLight, onError = OnErrorLight,
    outline = OutlineLight, outlineVariant = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark, onPrimary = OnPrimaryDark,
    primaryContainer = SecondaryDark, onPrimaryContainer = OnSecondaryDark,
    secondary = SecondaryDark, onSecondary = OnSecondaryDark,
    tertiary = TertiaryDark, onTertiary = OnPrimaryDark,
    background = BackgroundDark, onBackground = OnBackgroundDark,
    surface = SurfaceDark, onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark, onSurfaceVariant = OnSurfaceDark,
    error = ErrorDark, onError = OnErrorDark,
    outline = OutlineDark, outlineVariant = OutlineDark
)

@Composable
fun GradeFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colorScheme, typography = GradeFlowTypography, shapes = GradeFlowShapes, content = content)
}
