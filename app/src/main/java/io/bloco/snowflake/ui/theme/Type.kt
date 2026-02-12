package io.bloco.snowflake.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import io.bloco.snowflake.R

val spaceGroteskFamily = FontFamily(
    Font(
        R.font.space_grotesk_variable_font,
        weight = FontWeight(400),
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.space_grotesk_variable_font,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        R.font.space_grotesk_variable_font,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.space_grotesk_variable_font,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.space_grotesk_variable_font,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    ),
)

private val default = Typography()

val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = spaceGroteskFamily),
    displayMedium = default.displayMedium.copy(fontFamily = spaceGroteskFamily),
    displaySmall = default.displaySmall.copy(fontFamily = spaceGroteskFamily),
    headlineLarge = default.headlineLarge.copy(fontFamily = spaceGroteskFamily),
    headlineMedium = default.headlineMedium.copy(fontFamily = spaceGroteskFamily),
    headlineSmall = default.headlineSmall.copy(fontFamily = spaceGroteskFamily),
    titleLarge = default.titleLarge.copy(fontFamily = spaceGroteskFamily),
    titleMedium = default.titleMedium.copy(fontFamily = spaceGroteskFamily),
    titleSmall = default.titleSmall.copy(fontFamily = spaceGroteskFamily),
    bodyLarge = default.bodyLarge.copy(fontFamily = spaceGroteskFamily),
    bodyMedium = default.bodyMedium.copy(fontFamily = spaceGroteskFamily),
    bodySmall = default.bodySmall.copy(fontFamily = spaceGroteskFamily),
    labelLarge = default.labelLarge.copy(fontFamily = spaceGroteskFamily),
    labelMedium = default.labelMedium.copy(fontFamily = spaceGroteskFamily),
    labelSmall = default.labelSmall.copy(fontFamily = spaceGroteskFamily),
    displayLargeEmphasized = default.displayLargeEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.ExtraBold),
    displayMediumEmphasized = default.displayMediumEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.ExtraBold),
    displaySmallEmphasized = default.displaySmallEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.ExtraBold),
    headlineLargeEmphasized = default.headlineLargeEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    headlineMediumEmphasized = default.headlineMediumEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    headlineSmallEmphasized = default.headlineSmallEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    titleLargeEmphasized = default.titleLargeEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    titleMediumEmphasized = default.titleMediumEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    titleSmallEmphasized = default.titleSmallEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    bodyLargeEmphasized = default.bodyLargeEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    bodyMediumEmphasized = default.bodyMediumEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    bodySmallEmphasized = default.bodySmallEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    labelLargeEmphasized = default.labelLargeEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    labelMediumEmphasized = default.labelMediumEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
    labelSmallEmphasized = default.labelSmallEmphasized.copy(fontFamily = spaceGroteskFamily, fontWeight = FontWeight.Bold),
)
