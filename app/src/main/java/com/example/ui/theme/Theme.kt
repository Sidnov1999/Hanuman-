package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.Black,
    primaryContainer = SaffronDark,
    onPrimaryContainer = Color.White,
    secondary = SaffronLight,
    onSecondary = Color.Black,
    background = AmoledBackground,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = OnSurfaceLight,
    surfaceVariant = CardDark,
    onSurfaceVariant = OnSurfaceLight,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
