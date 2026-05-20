package io.bloco.snowflake.data

import io.bloco.snowflake.models.DayStats
import io.bloco.snowflake.models.StatsInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StatsStore(
    val updateStats: suspend (DayStats) -> Unit,
    val getLastDayStats: () -> Flow<DayStats?>,
    val getAllStats: () -> Flow<List<DayStats>>,
) {
    fun today() =
        getLastDayStats()
            // Ensure it's Today's since we may not have an entry yet
            .map { if (it?.date == LocalDate.now()) it else DayStats() }

    fun all() = getAllStats()

    suspend fun incrementInstant(instant: StatsInstant) {
        val previousValue = today().first()
        val newValue = previousValue.copy(
            failedConnections = previousValue.failedConnections + instant.failedConnectionCount,
            inboundBytes = previousValue.inboundBytes + instant.inboundBytes,
            outboundBytes = previousValue.outboundBytes + instant.outboundBytes,
        )
        updateStats(newValue)
    }

    suspend fun incrementConnections() {
        val previousValue = today().first()
        val newValue = previousValue.copy(
            connections = previousValue.connections + 1,
        )
        updateStats(newValue)
    }
}
