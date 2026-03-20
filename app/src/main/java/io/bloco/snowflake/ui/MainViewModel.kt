package io.bloco.snowflake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.bloco.snowflake.models.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MainViewModel(
    getAppConfig: () -> Flow<AppConfig>,
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        getAppConfig()
            .onEach { _state.update { state -> state.copy(isEnabled = it.isEnabled) } }
            .launchIn(viewModelScope)
    }

    data class State(
        val isEnabled: Boolean = false,
    )
}
