package io.bloco.snowflake.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween

fun <T> enabledTransitionSpec(): FiniteAnimationSpec<T> = tween(1500)
