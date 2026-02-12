package io.bloco.snowflake.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.bloco.snowflake.App
import io.bloco.snowflake.R
import io.bloco.snowflake.ui.MainActivity
import io.bloco.snowflake.ui.theme.PrimaryColor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class SnowflakeWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val dependencies by lazy { (appContext as App).dependencies }
    private val userPreferences by lazy { dependencies.appDataStore }
    private val snowflakeManager by lazy { dependencies.snowflakeManager }
    private val notificationManager by lazy { appContext.getSystemService<NotificationManager>() }
    private val resources by lazy { appContext.resources }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        buildNotificationChannelIfNeeded()
        val notification = buildNotification(SnowflakeManager.State.Running())
        return buildForegroundInfo(notification)
    }

    override suspend fun doWork(): Result {
        Timber.i("SnowflakeWorker: Start")
        try {
            setForeground(getForegroundInfo())
        } catch (e: IllegalStateException) {
            Timber.w(e)
            return Result.failure()
        }

        try {
            work()
        } catch (e: CancellationException) {
            if (isStopped) {
                Timber.w(
                    e,
                    "SnowflakeWorker: early stop %s",
                    if (Build.VERSION.SDK_INT >= 31) stopReason else null,
                )
            } else {
                Timber.i("SnowflakeWorker: cancelled")
            }
        } finally {
            snowflakeManager.stop()
            notificationManager?.cancel(NOTIFICATION_ID)
            Timber.i("SnowflakeWorker: finished")
        }
        return Result.success()
    }

    private suspend fun work() {
        if (!userPreferences.appConfig.first().isEnabled) {
            Timber.i("SnowflakeWorker: not enabled")
            return
        }

        snowflakeManager.start()

        var hasStarted = false

        snowflakeManager.state
            .onEach {
                if (it is SnowflakeManager.State.Running) {
                    hasStarted = true
                    notificationManager?.notify(NOTIFICATION_ID, buildNotification(it))
                }
            }
            .takeWhile { !hasStarted || it is SnowflakeManager.State.Running }
            .collect()
    }

    private fun buildNotificationChannelIfNeeded() {
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                resources.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
    }

    private fun buildForegroundInfo(notification: Notification): ForegroundInfo =
        if (Build.VERSION.SDK_INT >= 29) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
            )
        }

    private fun buildNotification(state: SnowflakeManager.State.Running): Notification =
        NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_snowflake)
            .setColor(PrimaryColor.toArgb())
            .setContentTitle(resources.getString(R.string.notification_channel))
            .setContentText(
                resources.getString(
                    if (state.clientConnected) {
                        R.string.notification_text_connected
                    } else {
                        R.string.notification_text
                    }
                )
            )
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSound(null)
            .setVibrate(null)
            .setOnlyAlertOnce(true)
            .setLights(0, 0, 0)
            .setContentIntent(
                PendingIntent.getActivity(
                    appContext,
                    0,
                    Intent(appContext, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "snowflake"
        private const val NOTIFICATION_ID = 11
    }
}