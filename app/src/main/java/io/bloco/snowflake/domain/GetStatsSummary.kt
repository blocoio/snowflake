package io.bloco.snowflake.domain

import io.bloco.snowflake.models.StatsInstant
import io.bloco.snowflake.models.StatsSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class GetStatsSummary(
    private val getStatsInstants: () -> Flow<List<StatsInstant>>,
    private val getClientConnections: () -> Flow<List<Instant>>,
) {

    operator fun invoke() =
        combine(
            getStatsInstants(),
            getClientConnections()
        ) { stats, connections ->
            val ago24 = Clock.System.now() - 24.hours
            val last24Stats = stats.filter { it.timestamp > ago24 }
            StatsSummary(
                clientConnections = connections.count { it > ago24 }.toLong(),
                inboundBytes = last24Stats.sumOf { it.inboundBytes },
                outboundBytes = last24Stats.sumOf { it.inboundBytes },
                inboundUnit = "B",
                outboundUnit = "B",
            )
        }
}