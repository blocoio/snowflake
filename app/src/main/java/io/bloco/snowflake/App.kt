package io.bloco.snowflake

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class App : Application() {
    val dependencies by lazy { Dependencies(this) }

    private val appScope by lazy { CoroutineScope(Dispatchers.IO) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appScope.launch {
            dependencies.refreshStunServers()
        }

        // Configure workers

        onAppOpen()
            .flatMapLatest {
                dependencies
                    .appDataStore
                    .appConfig
                    .onEach(dependencies.configureWorkers::background)
            }.launchIn(appScope)

        onAppOpen()
            .flatMapLatest {
                dependencies
                    .appDataStore
                    .appConfig
                    .onEach(dependencies.configureWorkers::foreground)
            }.launchIn(appScope)
    }

    private fun onAppOpen() =
        dependencies
            .monitorAppOpen
            .state
            .filter { it }
}
