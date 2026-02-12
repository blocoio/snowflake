package io.bloco.snowflake.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.bloco.snowflake.background.SnowflakeManager
import io.bloco.snowflake.common.PublishFlow
import io.bloco.snowflake.models.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class HomeViewModel(
    openAbout: () -> Unit,
    openSettings: () -> Unit,
    getSnowflakeState: () -> Flow<SnowflakeManager.State>,
    getAppConfig: () -> Flow<AppConfig>,
    setIsEnabled: suspend (Boolean) -> Unit,
    isIgnoringBatteryOptimizations: () -> Flow<Boolean>
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val events = PublishFlow<Event>()
    fun onEvent(event: Event) = events.tryEmit(event)

    init {
        getSnowflakeState()
            .onEach { _state.update { state -> state.copy(snowflakeState = it) } }
            .launchIn(viewModelScope)

        getAppConfig()
            .onEach { _state.update { state -> state.copy(config = it) } }
            .launchIn(viewModelScope)

        isIgnoringBatteryOptimizations()
            .onEach { _state.update { state -> state.copy(isIgnoringBatteryOptimizations = it) } }
            .launchIn(viewModelScope)

        events
            .filterIsInstance<Event.EnabledChange>()
            .onEach { setIsEnabled(it.value) }
            .launchIn(viewModelScope)

        events
            .filterIsInstance<Event.AboutClick>()
            .onEach { openAbout() }
            .launchIn(viewModelScope)

        events
            .filterIsInstance<Event.SettingsClick>()
            .onEach { openSettings() }
            .launchIn(viewModelScope)
    }

    data class State(
        val snowflakeState: SnowflakeManager.State = SnowflakeManager.State.Stopped,
        val config: AppConfig? = null,
        val isIgnoringBatteryOptimizations: Boolean? = null,
    )

    sealed interface Event {
        data class EnabledChange(val value: Boolean) : Event
        data object AboutClick : Event
        data object SettingsClick : Event
    }
}