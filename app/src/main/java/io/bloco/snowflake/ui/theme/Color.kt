package io.bloco.snowflake.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SeedColor = Color(0xFF2B5C62)

val EnabledBackgroundLight = Color(0xFFF5EDDD)

@Composable
fun ColorScheme.appBackground(isEnabled: Boolean) = if (isEnabled && !isSystemInDarkTheme()) EnabledBackgroundLight else background
