package io.bloco.snowflake.background

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import io.bloco.snowflake.App
import io.bloco.snowflake.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SnowflakeTileService : TileService() {
    val dependencies get() = (application as App).dependencies
    var coroutineScope: CoroutineScope? = null

    override fun onStartListening() {
        super.onStartListening()

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        this.coroutineScope = coroutineScope

        qsTile.label = getString(R.string.app_name)
        qsTile.updateTile()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dependencies.snowflakeManager
                .state
                .onEach { state ->
                    qsTile.subtitle = state.subtitle
                    qsTile.updateTile()
                }.launchIn(coroutineScope)
        }

        dependencies.appDataStore
            .appConfig
            .map { it.isEnabled }
            .distinctUntilChanged()
            .onEach { isEnabled ->
                qsTile.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                qsTile.updateTile()
            }.launchIn(coroutineScope)
    }

    override fun onStopListening() {
        super.onStopListening()
        coroutineScope?.cancel()
        coroutineScope = null
    }

    override fun onClick() {
        super.onClick()
        coroutineScope?.launch {
            val isEnabled = dependencies.appDataStore.appConfig
                .first()
                .isEnabled
            dependencies.appDataStore.setSnowflakeEnabled(!isEnabled)
        }
    }

    private val SnowflakeManager.State.subtitle get() =
        when (this) {
            is SnowflakeManager.State.Running ->
                if (clientsConnected > 0) {
                    getString(R.string.quick_settings_helping, clientsConnected)
                } else {
                    getString(R.string.quick_settings_looking)
                }

            SnowflakeManager.State.Stopped -> getString(R.string.snowflake_stopped)
        }
}
