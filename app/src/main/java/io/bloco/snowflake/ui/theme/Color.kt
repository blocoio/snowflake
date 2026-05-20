package io.bloco.snowflake.ui.theme

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import io.bloco.snowflake.ui.theme.enabledTransitionSpec

val SeedColor = Color(0xFF2B5C62)

val EnabledBackgroundLight = Color(0xFFF5EDDD)

@Composable
fun ColorScheme.appBackground(isEnabled: Boolean): Color {
    val enabledTransition = updateTransition(isEnabled, label = "enabled")
    val background by enabledTransition.animateColor(
        transitionSpec = { enabledTransitionSpec() },
    ) {
        if (it && !isSystemInDarkTheme()) EnabledBackgroundLight else background
    }
    return background
}
