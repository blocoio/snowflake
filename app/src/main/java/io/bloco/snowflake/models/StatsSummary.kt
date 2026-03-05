package io.bloco.snowflake.models

data class StatsSummary(
    val clientConnections: Long,
    val inboundBytes: Long,
    val outboundBytes: Long,
    val inboundUnit: String?,
    val outboundUnit: String?,
)