package io.bloco.snowflake.background

import IPtProxy.SnowflakeClientEvents
import IPtProxy.SnowflakeProxy
import io.bloco.snowflake.common.convertToBytes
import io.bloco.snowflake.models.SnowflakeConfig
import io.bloco.snowflake.models.StatsInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SnowflakeManager(
    snowflakeProxyProvider: () -> SnowflakeProxy,
    private val backgroundContext: CoroutineContext,
    private val getSnowflakeConfig: () -> Flow<SnowflakeConfig>,
    private val storeStatsInstant: suspend (StatsInstant) -> Unit,
    private val storeClientConnection: suspend () -> Unit,
) {

    private val _state = MutableStateFlow<State>(State.Stopped)
    val state get() = _state.asStateFlow()
    private val snowflakeProxy by lazy(snowflakeProxyProvider)

    suspend fun start() {
        withContext(backgroundContext) {
            if (_state.value != State.Stopped) return@withContext

            if (DUMMY_MODE) {
                _state.value = State.Running()
                return@withContext
            }

            if (!snowflakeProxy.isRunning) {
                snowflakeProxy.applyConfig(getSnowflakeConfig().first())
                snowflakeProxy.clientEvents = clientEvents
                snowflakeProxy.start()
                _state.value = State.Running()
            }
        }
    }

    fun stop() {
        if (_state.value !is State.Running) return
        if (!DUMMY_MODE) {
            if (snowflakeProxy.isRunning) {
                snowflakeProxy.stop()
            }
        }
        _state.value = State.Stopped
    }

    private fun SnowflakeProxy.applyConfig(config: SnowflakeConfig) {
        capacity = config.capacity.value
        brokerUrl = config.brokerUrl
        relayUrl = config.relayUrl
        stunServer = config.stunServer
        natProbeUrl = config.natProbeUrl
        pollInterval = config.pollInterval.inWholeSeconds
        summaryInterval = config.summaryInterval.inWholeSeconds
        proxyTypeIdentifier = config.proxyTypeIdentifier
    }

    private val clientEvents = object : SnowflakeClientEvents {
        override fun connected() {
            Timber.i("connected")
            _state.update {
                if (it is State.Running) {
                    it.copy(clientsConnected = it.clientsConnected + 1)
                } else it
            }
            CoroutineScope(backgroundContext).launch {
                storeClientConnection()
            }
        }

        override fun connectionFailed() {
            Timber.i("connectionFailed")
        }

        override fun disconnected(country: String?) {
            Timber.i("disconnected $country")
            _state.update {
                if (it is State.Running) {
                    it.copy(
                        clientsConnected = (it.clientsConnected - 1)
                            .coerceAtLeast(0)
                    )
                } else it
            }
        }

        override fun stats(
            connectionCount: Long,
            failedConnectionCount: Long,
            inboundBytes: Long,
            outboundBytes: Long,
            inboundUnit: String?,
            outboundUnit: String?,
            summaryInterval: Long,
        ) {
            if (summaryInterval == 0L) return
            if (inboundBytes == 0L && outboundBytes == 0L && failedConnectionCount == 0L) return

            CoroutineScope(backgroundContext).launch {
                storeStatsInstant(
                    StatsInstant(
                        failedConnectionCount = failedConnectionCount,
                        inboundBytes = convertToBytes(inboundBytes, inboundUnit),
                        outboundBytes = convertToBytes(outboundBytes, outboundUnit),
                    )
                )
            }

            Timber.i(
                """
                            stats
                            connectionCount = $connectionCount
                            failedConnectionCount = $failedConnectionCount
                            inboundBytes = $inboundBytes
                            outboundBytes = $outboundBytes
                            inboundUnit = $inboundUnit
                            outboundUnit = $outboundUnit
                            summaryInterval = $summaryInterval
                            """.trimIndent()
            )
        }
    }

    sealed interface State {
        data class Running(val clientsConnected: Int = 0) : State
        data object Stopped : State
    }

    companion object {
        private const val DUMMY_MODE = false
    }
}