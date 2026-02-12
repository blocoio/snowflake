package io.bloco.snowflake.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.bloco.snowflake.models.AppConfig
import io.bloco.snowflake.models.Capacity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

class AppDataStore(
    private val dataStoreProvider: () -> DataStore<Preferences>
) {

    // App Config

    val appConfig: Flow<AppConfig> =
        dataStoreFlow()
            .flatMapLatest { dataStore ->
                dataStore.data.map {
                    AppConfig(
                        isEnabled = it[SNOWFLAKE_ENABLED] == true,
                        background = it[BACKGROUND] ?: true,
                        unmeteredOnly = it[UNMETERED_ONLY] ?: true,
                        chargingOnly = it[CHARGING_ONLY] ?: false,
                    )
                }
            }
            .distinctUntilChanged()

    suspend fun setSnowflakeEnabled(value: Boolean) =
        dataStore().edit { it[SNOWFLAKE_ENABLED] = value }

    suspend fun setBackground(value: Boolean) =
        dataStore().edit { it[BACKGROUND] = value }

    suspend fun setUnmeteredOnly(value: Boolean) =
        dataStore().edit { it[UNMETERED_ONLY] = value }

    suspend fun setChargingOnly(value: Boolean) =
        dataStore().edit { it[CHARGING_ONLY] = value }

    // Capacity

    val capacity
        get() = dataStoreFlow()
            .flatMapLatest { dataStore -> dataStore.data.map { Capacity.fromValue(it[CAPACITY]) } }
            .distinctUntilChanged()

    suspend fun setCapacity(capacity: Capacity) =
        dataStore().edit { it[CAPACITY] = capacity.value }

    // STUN Servers

    val stunServers
        get() = dataStoreFlow()
            .flatMapLatest { dataStore -> dataStore.data.map { it[STUN_SERVERS]?.toList() } }
            .distinctUntilChanged()

    suspend fun setStunServers(stunServers: List<String>) =
        dataStore().edit { it[STUN_SERVERS] = stunServers.toSet() }

    val stunServersDate
        get() = dataStoreFlow()
            .flatMapLatest { dataStore ->
                dataStore.data.map { data ->
                    data[STUN_SERVERS_DATE]?.let { Instant.fromEpochSeconds(it) }
                }
            }
            .distinctUntilChanged()

    suspend fun setStunServersDate(date: Instant) =
        dataStore().edit { it[STUN_SERVERS_DATE] = date.epochSeconds }

    // Internal

    private var _dataStore: DataStore<Preferences>? = null

    private fun dataStoreFlow(): Flow<DataStore<Preferences>> {
        return _dataStore
            ?.let(::flowOf)
            ?: flow {
                emit(
                    dataStoreProvider()
                        .also { this@AppDataStore._dataStore = it }
                )
            }
    }

    private suspend fun dataStore() = dataStoreFlow().first()

    companion object Companion {
        private val SNOWFLAKE_ENABLED = booleanPreferencesKey("snowflake_enabled")
        private val BACKGROUND = booleanPreferencesKey("background")
        private val UNMETERED_ONLY = booleanPreferencesKey("unmetered_only")
        private val CHARGING_ONLY = booleanPreferencesKey("charging_only")
        private val CAPACITY = longPreferencesKey("capacity")
        private val STUN_SERVERS = stringSetPreferencesKey("stun")
        private val STUN_SERVERS_DATE = longPreferencesKey("stun_date")
    }
}
