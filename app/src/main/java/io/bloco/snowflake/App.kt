package io.bloco.snowflake

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.bloco.snowflake.background.SnowflakeWorker
import io.bloco.snowflake.models.AppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class App : Application() {
    val dependencies by lazy { Dependencies(this) }
    private val workManager by lazy { WorkManager.getInstance(this) }
    private val appScope by lazy { CoroutineScope(Dispatchers.IO) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appScope.launch {
            dependencies.refreshStunServers()
        }

        dependencies
            .appDataStore
            .appConfig
            .distinctUntilChanged()
            .onEach(::configureBackgroundWork)
            .launchIn(appScope)
    }

    private fun configureBackgroundWork(config: AppConfig) {
        val constraints = config.toConstraints()

        if (config.isEnabled && config.background) {
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = WORK_NAME_BACKGROUND,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = PeriodicWorkRequestBuilder<SnowflakeWorker>(
                    repeatInterval = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                    repeatIntervalTimeUnit = TimeUnit.MILLISECONDS
                )
                    .setConstraints(constraints)
                    .build(),
            )
        } else {
            workManager.cancelUniqueWork(WORK_NAME_BACKGROUND)
        }

        if (config.isEnabled) {
            workManager.enqueueUniqueWork(
                uniqueWorkName = WORK_NAME_ONE_OFF,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = OneTimeWorkRequestBuilder<SnowflakeWorker>()
                    .setConstraints(constraints)
                    .build(),
            )
        } else {
            workManager.cancelUniqueWork(WORK_NAME_ONE_OFF)
        }
    }

    private fun AppConfig.toConstraints() =
        Constraints.Builder()
            .run { setRequiredNetworkType(if (unmeteredOnly) NetworkType.UNMETERED else NetworkType.CONNECTED) }
            .setRequiresCharging(chargingOnly)
            .build()

    companion object {
        private const val WORK_NAME_ONE_OFF = "snowflake"
        private const val WORK_NAME_BACKGROUND = "snowflake_background"
    }
}
