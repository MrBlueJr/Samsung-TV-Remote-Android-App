package com.vibecode.tvremote.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GlowCyan,
    secondary = GlowPurple,
    background = ObsidianBg,
    surface = DarkCardBg,
    onPrimary = ObsidianBg,
    onSecondary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite
)

@Composable
fun TvRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
