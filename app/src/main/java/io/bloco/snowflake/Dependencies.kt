package io.bloco.snowflake

import IPtProxy.SnowflakeProxy
import android.app.Application
import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.bloco.snowflake.background.BatteryOptimization
import io.bloco.snowflake.background.SnowflakeManager
import io.bloco.snowflake.data.AppDataStore
import io.bloco.snowflake.data.FetchStunServers
import io.bloco.snowflake.domain.GetSnowflakeConfig
import io.bloco.snowflake.domain.RefreshStunServers
import io.bloco.snowflake.ui.home.HomeViewModel
import io.bloco.snowflake.ui.settings.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

class Dependencies(app: Application) {
    private val backgroundContext = Dispatchers.IO
    private val powerManager = app.getSystemService<PowerManager>()!!

    // Data

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("preferences")
    private val dataStoreProvider = { app.dataStore }
    private val httpClientProvider by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    val appDataStore by lazy { AppDataStore(dataStoreProvider) }
    val fetchStunServers by lazy { FetchStunServers(::httpClientProvider) }

    // Background

    val batteryOptimization by lazy { BatteryOptimization(powerManager, app) }

    private val snowflakeProxy: SnowflakeProxy by lazy { SnowflakeProxy() }

    val snowflakeManager by lazy {
        SnowflakeManager(
            snowflakeProxyProvider = { snowflakeProxy },
            backgroundContext = backgroundContext,
            getSnowflakeConfig = getSnowflakeConfig::invoke,
        )
    }

    // Domain

    private val getSnowflakeConfig by lazy {
        GetSnowflakeConfig(
            getCapacity = appDataStore::capacity,
            getStunServers = appDataStore::stunServers,
        )
    }
    val refreshStunServers by lazy {
        RefreshStunServers(
            fetchStunServers = fetchStunServers::invoke,
            getStunServersDate = appDataStore::stunServersDate,
            setStunServersDate = appDataStore::setStunServersDate,
            setStunServers = appDataStore::setStunServers,
        )
    }

    // View Models

    fun homeViewModel(
        openAbout: () -> Unit,
        openSettings: () -> Unit,
    ) = HomeViewModel(
        openAbout = openAbout,
        openSettings = openSettings,
        getSnowflakeState = snowflakeManager::state,
        getAppConfig = appDataStore::appConfig,
        setIsEnabled = appDataStore::setSnowflakeEnabled,
        isIgnoringBatteryOptimizations = batteryOptimization::isIgnoring,
    )

    fun settingsViewModel(
        goBack: () -> Unit,
    ) = SettingsViewModel(
        goBack = goBack,
        getAppConfig = appDataStore::appConfig,
        getCapacity = appDataStore::capacity,
        setUnmeteredOnly = appDataStore::setUnmeteredOnly,
        setChargingOnly = appDataStore::setChargingOnly,
        setCapacity = appDataStore::setCapacity,
    )
}