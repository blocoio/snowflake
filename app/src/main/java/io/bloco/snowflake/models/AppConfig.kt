package io.bloco.snowflake.models

data class AppConfig(
    val isEnabled: Boolean,
    val background: Boolean,
    val unmeteredOnly: Boolean,
    val chargingOnly: Boolean,
)
