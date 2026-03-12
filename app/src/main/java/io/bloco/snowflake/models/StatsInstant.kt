package io.bloco.snowflake.models

data class StatsInstant(
    val failedConnectionCount: Long,
    val inboundBytes: Long,
    val outboundBytes: Long,
)