package io.bloco.snowflake.background

import IPtProxy.SnowflakeClientEvents
import IPtProxy.SnowflakeProxy
import io.bloco.snowflake.models.SnowflakeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SnowflakeManager(
    snowflakeProxyProvider: () -> SnowflakeProxy,
    private val backgroundContext: CoroutineContext,
    private val getSnowflakeConfig: () -> Flow<SnowflakeConfig>,
) {

    private val _state = MutableStateFlow<State>(State.Stopped)
    val state get() = _state.asStateFlow()
    private val snowflakeProxy by lazy(snowflakeProxyProvider)

    suspend fun start() {
        withContext(backgroundContext) {
            if (_state.value != State.Stopped) return@withContext

            if (!DUMMY_MODE) {
                if (!snowflakeProxy.isRunning) {
                    snowflakeProxy.applyConfig(getSnowflakeConfig().first())
                    snowflakeProxy.clientEvents = object : SnowflakeClientEvents {
                        override fun connected() {
                            Timber.i("connected")
                            _state.value = State.Running(true)
                        }

                        override fun connectionFailed() {
                            Timber.i("connectionFailed")
                        }

                        override fun disconnected(country: String?) {
                            Timber.i("disconnected $country")
                            _state.value = State.Running(false)
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
                            if (
                                connectionCount == 0L
                                && failedConnectionCount == 0L
                                && summaryInterval == 0L
                            ) return
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
                    snowflakeProxy.start()
                }
            }

            _state.value = State.Running(false)
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
        pollInterval = config.pollInterval
        proxyTypeIdentifier = config.proxyTypeIdentifier
    }

    sealed interface State {
        data class Running(val clientConnected: Boolean = false) : State
        data object Stopped : State
    }

    companion object {
        private const val DUMMY_MODE = false
    }
}