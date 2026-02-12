package io.bloco.snowflake.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.bloco.snowflake.background.SnowflakeManager
import io.bloco.snowflake.common.PublishFlow
import io.bloco.snowflake.models.AppConfig
import io.bloco.snowflake.models.Capacity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    goBack: () -> Unit,
    getAppConfig: () -> Flow<AppConfig>,
    getCapacity: () -> Flow<Capacity>,
    setUnmeteredOnly: suspend (Boolean) -> Unit,
    setChargingOnly: suspend (Boolean) -> Unit,
    setCapacity: suspend (Capacity) -> Unit,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val events = PublishFlow<Event>()
    fun onEvent(event: Event) = events.tryEmit(event)

    init {
        getAppConfig()
            .onEach { _state.update { state -> state.copy(config = it) } }
            .launchIn(viewModelScope)

        getCapacity()
            .onEach { _state.update { state -> state.copy(capacity = it) } }
            .launchIn(viewModelScope)

        events
            .filterIsInstance<Event.BackClick>()
            .onEach { goBack() }
            .launchIn(viewModelScope)

        events
            .filterIsInstance<Event.UnmeteredOnlyChange>()
            .onEach { setUnmeteredOnly(it.value) }
            .launchIn(viewModelScope)

        events
            .filterIsInstance<Event.ChargingOnlyChange>()
            .onEach { setChargingOnly(it.value) }
            .launchIn(viewModelScope)
        events
            .filterIsInstance<Event.CapacityChange>()
            .onEach { setCapacity(it.value) }
            .launchIn(viewModelScope)

    }

    data class State(
        val config: AppConfig? = null,
        val capacity: Capacity? = null,
    )

    sealed interface Event {
        data object BackClick : Event
        data class UnmeteredOnlyChange(val value: Boolean) : Event
        data class ChargingOnlyChange(val value: Boolean) : Event
        data class CapacityChange(val value: Capacity) : Event
    }
}