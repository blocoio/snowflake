package io.bloco.snowflake.background

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonitorAppOpen {
    private val _state = MutableStateFlow(false)
    val state get() = _state.asStateFlow()

    fun markAppAsOpened() {
        _state.value = true
    }
}
