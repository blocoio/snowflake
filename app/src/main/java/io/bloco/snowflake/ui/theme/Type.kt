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
    )

val Typography.titleMediumEmphasized
    get() = titleMedium.copy(fontWeight = FontWeight.Bold)
