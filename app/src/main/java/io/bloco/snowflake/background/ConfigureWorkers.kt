package io.bloco.snowflake.background

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.bloco.snowflake.models.AppConfig
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours

class ConfigureWorkers(
    private val workManager: WorkManager,
) {
    fun background(config: AppConfig) {
        if (config.isEnabled && config.background) {
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = WORK_NAME_BACKGROUND,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = PeriodicWorkRequestBuilder<SnowflakeWorker>(
                    repeatInterval = PERIODIC_INTERVAL.inWholeMilliseconds,
                    repeatIntervalTimeUnit = TimeUnit.MILLISECONDS,
                ).setInitialDelay(
                    duration = PERIODIC_INTERVAL.inWholeMilliseconds,
                    timeUnit = TimeUnit.MILLISECONDS,
                ).setConstraints(config.toConstraints())
                    .build(),
            )
        } else {
            workManager.cancelUniqueWork(WORK_NAME_BACKGROUND)
        }
    }

    fun foreground(config: AppConfig) {
        if (config.isEnabled) {
            workManager.enqueueUniqueWork(
                uniqueWorkName = WORK_NAME_ONE_OFF,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = OneTimeWorkRequestBuilder<SnowflakeWorker>()
                    .setConstraints(config.toConstraints())
                    .build(),
            )
        } else {
            workManager.cancelUniqueWork(WORK_NAME_ONE_OFF)
        }
    }

    private fun AppConfig.toConstraints() =
        Constraints
            .Builder()
            .setRequiredNetworkType(
                if (unmeteredOnly) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                },
            ).setRequiresCharging(chargingOnly)
            .build()

    companion object {
        private const val WORK_NAME_ONE_OFF = "snowflake"
        private const val WORK_NAME_BACKGROUND = "snowflake_background"
        private val PERIODIC_INTERVAL = 2.hours
    }
}
