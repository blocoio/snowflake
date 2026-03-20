package io.bloco.snowflake.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme

@Composable
fun SnowflakeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = getColorScheme(darkTheme),
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}

@Composable
private fun getColorScheme(darkTheme: Boolean): ColorScheme =
    rememberDynamicColorScheme(
        seedColor = SeedColor,
        style = PaletteStyle.TonalSpot,
        // specVersion = ColorSpec.SpecVersion.SPEC_2025,
        isDark = darkTheme,
        isAmoled = true,
    )
