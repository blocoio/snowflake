package io.bloco.snowflake.models

import kotlin.time.Instant

data class StatsInstant(
    val timestamp: Instant,
    val connectionCount: Long,
    val failedConnectionCount: Long,
    val inboundBytes: Long,
    val outboundBytes: Long,
    val inboundUnit: String?,
    val outboundUnit: String?,
    val summaryInterval: Long,
)