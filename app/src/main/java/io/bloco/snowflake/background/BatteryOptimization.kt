package io.bloco.snowflake.background

import android.content.Context
import android.os.PowerManager
import io.bloco.snowflake.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BatteryOptimization(
    private val powerManager: PowerManager,
    private val context: Context,
) {

    private val _isIgnoring = MutableStateFlow(false)
    val isIgnoring get() = _isIgnoring.asStateFlow()

    init {
        check()
    }

    fun check() {
        _isIgnoring.value = powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun requestIgnore(activity: MainActivity) {
        activity.requestIgnoreBatteryOptimization {
            check()
        }
    }
}