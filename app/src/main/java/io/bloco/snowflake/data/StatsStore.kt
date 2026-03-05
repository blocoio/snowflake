package io.bloco.snowflake.data

import io.bloco.snowflake.models.StatsInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

class StatsStore {

    private val _instants = MutableStateFlow(emptyList<StatsInstant>())
    val instants get() = _instants.asStateFlow()

    private val _clientConnections = MutableStateFlow(emptyList<Instant>())
    val clientConnections = _clientConnections.asStateFlow()

    suspend fun storeInstant(instant: StatsInstant) {
        _instants.update { it + instant }
    }

    suspend fun storeConnection(timestamp: Instant) {
        _clientConnections.update { it + timestamp }
    }
}