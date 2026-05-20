package io.bloco.snowflake.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.bloco.snowflake.models.DayStats
import io.bloco.snowflake.models.sum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class StatsViewModel(
    getAllStats: () -> Flow<List<DayStats>>,
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        getAllStats()
            .map(::buildState)
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    private fun buildState(stats: List<DayStats>): State {
        val today = LocalDate.now()
        val firstDate = stats.firstOrNull()?.date ?: today
        val days = (0.until(LAST_N_DAYS))
            .map { today.minusDays(it) }
            .filter { it >= firstDate }
            .map { day -> stats.firstOrNull { it.date == day } ?: DayStats(date = day) }
        val months = stats
            .filter { it.date >= today.withDayOfMonth(1).minusMonths(LAST_N_MONTHS) }
            .groupBy { it.date.withDayOfMonth(1) }
            .map { (month, stats) ->
                (stats.sum() ?: DayStats()).copy(date = month)
            }
        val years = stats
            .groupBy { it.date.withDayOfYear(1) }
            .map { (year, stats) ->
                (stats.sum() ?: DayStats()).copy(date = year)
            }
        return State(
            days = days,
            months = months,
            years = years,
            total = stats.sum() ?: DayStats(),
        )
    }

    data class State(
        val days: List<DayStats> = emptyList(),
        val months: List<DayStats> = emptyList(),
        val years: List<DayStats> = emptyList(),
        val total: DayStats? = null,
    )

    companion object {
        private const val LAST_N_DAYS = 5L
        private const val LAST_N_MONTHS = 12L
    }
}
