package io.bloco.snowflake.data

import io.bloco.snowflake.models.DayStats
import io.bloco.snowflake.models.StatsInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StatsStore(
    val storeStats: suspend (DayStats) -> Unit,
    val getStatsByDate: (LocalDate) -> Flow<DayStats?>,
) {
    fun today() = getStatsByDate(LocalDate.now()).map { it ?: DayStats() }

    suspend fun incrementInstant(instant: StatsInstant) {
        val previousValue = today().first()
        val newValue =
            previousValue.copy(
                failedConnections = previousValue.failedConnections + instant.failedConnectionCount,
                inboundBytes = previousValue.inboundBytes + instant.inboundBytes,
                outboundBytes = previousValue.outboundBytes + instant.outboundBytes,
            )
        storeStats(newValue)
    }

    suspend fun incrementConnections() {
        val previousValue = today().first()
        val newValue =
            previousValue.copy(
                connections = previousValue.connections + 1,
            )
        storeStats(newValue)
    }
}
