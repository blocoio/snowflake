package io.bloco.snowflake.background

import IPtProxy.SnowflakeClientEvents
import IPtProxy.SnowflakeProxy
import io.bloco.snowflake.common.convertToBytes
import io.bloco.snowflake.models.SnowflakeConfig
import io.bloco.snowflake.models.StatsInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class SnowflakeManager(
    snowflakeProxyProvider: () -> SnowflakeProxy,
    private val backgroundContext: CoroutineContext,
    private val getSnowflakeConfig: () -> Flow<SnowflakeConfig>,
    private val storeStatsInstant: suspend (StatsInstant) -> Unit,
    private val storeClientConnection: suspend () -> Unit,
    private val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic,
) {
    private val _state = MutableStateFlow<State>(State.Stopped)
    val state get() = _state.asStateFlow()
    private val snowflakeProxy by lazy(snowflakeProxyProvider)

    private val lifecycleMutex = Mutex()
    private var lastGoStop: ComparableTimeMark? = null

    suspend fun start() {
        if (!_state.compareAndSet(State.Stopped, State.Starting)) return

        delay(remainingCooldown())

        lifecycleMutex.withLock {
            withContext(backgroundContext) {
                // A stop() happened while we were waiting
                if (_state.value != State.Starting) return@withContext
                if (snowflakeProxy.isRunning) {
                    _state.value = State.Running()
                    return@withContext
                }

                snowflakeProxy.applyConfig(getSnowflakeConfig().first())
                snowflakeProxy.clientEvents = clientEvents
                snowflakeProxy.start()
                _state.value = State.Running()
            }
        }
    }

    suspend fun stop() {
        withContext(NonCancellable) {
            lifecycleMutex.withLock {
                withContext(backgroundContext) {
                    if (snowflakeProxy.isRunning) {
                        snowflakeProxy.stop()
                        lastGoStop = timeSource.markNow()
                    }
                    _state.value = State.Stopped
                }
            }
        }
    }

    private fun remainingCooldown(): Duration {
        val elapsed = lastGoStop?.elapsedNow() ?: return Duration.ZERO
        return (RESTART_COOLDOWN - elapsed).coerceAtLeast(Duration.ZERO)
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
        covertDTLSConfig = config.covertDTLSConfig
    }

    private inline fun onClientEvent(
        name: String,
        block: () -> Unit = {},
    ) {
        Timber.i("connected")
        try {
            block()
        } catch (t: Throwable) {
            Timber.e(t, "SnowflakeClientEvents.%s threw", name)
        }
    }

    private val clientEvents =
        object : SnowflakeClientEvents {
            override fun connected() =
                onClientEvent("connected") {
                    _state.update {
                        if (it !is State.Running) return@update it
                        it.copy(clientsConnected = it.clientsConnected + 1)
                    }
                    CoroutineScope(backgroundContext).launch {
                        storeClientConnection()
                    }
                }

            override fun connectionFailed() = onClientEvent("connectionFailed")

            override fun disconnected(country: String?) =
                onClientEvent("disconnected $country") {
                    _state.update {
                        if (it !is State.Running) return@update it
                        it.copy(
                            clientsConnected =
                                (it.clientsConnected - 1)
                                    .coerceAtLeast(0),
                        )
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
            ) = onClientEvent("stats") {
                if (summaryInterval == 0L) return@onClientEvent
                if (inboundBytes == 0L && outboundBytes == 0L && failedConnectionCount == 0L) return@onClientEvent

                CoroutineScope(backgroundContext).launch {
                    storeStatsInstant(
                        StatsInstant(
                            failedConnectionCount = failedConnectionCount,
                            inboundBytes = convertToBytes(inboundBytes, inboundUnit),
                            outboundBytes = convertToBytes(outboundBytes, outboundUnit),
                        ),
                    )
                }

                Timber.i(
                    """
                    connectionCount = $connectionCount
                    failedConnectionCount = $failedConnectionCount
                    inboundBytes = $inboundBytes
                    outboundBytes = $outboundBytes
                    inboundUnit = $inboundUnit
                    outboundUnit = $outboundUnit
                    summaryInterval = $summaryInterval
                    """.trimIndent(),
                )
            }

            override fun natTypeUpdated(natType: String?) = onClientEvent("natTypeUpdated $natType")
        }

    sealed interface State {
        data class Running(
            val clientsConnected: Int = 0,
        ) : State

        data object Starting : State

        data object Stopped : State
    }

    companion object {
        // Avoid a potential snowflake library issue with quick start/stops
        private val RESTART_COOLDOWN = 30.seconds
    }
}
