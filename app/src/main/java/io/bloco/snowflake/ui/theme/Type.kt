package io.bloco.snowflake.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import io.bloco.snowflake.R

val customFont =
    FontFamily(
        Font(
            R.font.hepta_slab_variable_font,
            weight = FontWeight(400),
            variationSettings = FontVariation.Settings(FontVariation.weight(400)),
        ),
        Font(
            R.font.hepta_slab_variable_font,
            weight = FontWeight.SemiBold,
            variationSettings = FontVariation.Settings(FontVariation.weight(600)),
        ),
        Font(
            R.font.hepta_slab_variable_font,
            weight = FontWeight.Bold,
            variationSettings = FontVariation.Settings(FontVariation.weight(700)),
        ),
    )

private val default = Typography()

val Typography =
    Typography(
        displayLarge = default.displayLarge.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        displayMedium = default.displayMedium.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        displaySmall = default.displaySmall.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        headlineLarge = default.headlineLarge.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        headlineMedium = default.headlineMedium.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        headlineSmall = default.headlineSmall.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        titleLarge = default.titleLarge.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        titleMedium = default.titleMedium.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        titleSmall = default.titleSmall.copy(fontFamily = customFont, fontWeight = FontWeight.SemiBold),
        bodyLarge = default.bodyLarge.copy(fontFamily = customFont),
        bodyMedium = default.bodyMedium.copy(fontFamily = customFont),
        bodySmall = default.bodySmall.copy(fontFamily = customFont),
        labelLarge = default.labelLarge.copy(fontFamily = customFont),
        labelMedium = default.labelMedium.copy(fontFamily = customFont),
        labelSmall = default.labelSmall.copy(fontFamily = customFont),
        displayLargeEmphasized = default.displayLargeEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        displayMediumEmphasized = default.displayMediumEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        displaySmallEmphasized = default.displaySmallEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        headlineLargeEmphasized = default.headlineLargeEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        headlineMediumEmphasized = default.headlineMediumEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        headlineSmallEmphasized = default.headlineSmallEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        titleLargeEmphasized = default.titleLargeEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        titleMediumEmphasized = default.titleMediumEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        titleSmallEmphasized = default.titleSmallEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        bodyLargeEmphasized = default.bodyLargeEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        bodyMediumEmphasized = default.bodyMediumEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        bodySmallEmphasized = default.bodySmallEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        labelLargeEmphasized = default.labelLargeEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        labelMediumEmphasized = default.labelMediumEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
        labelSmallEmphasized = default.labelSmallEmphasized.copy(fontFamily = customFont, fontWeight = FontWeight.Bold),
    )
