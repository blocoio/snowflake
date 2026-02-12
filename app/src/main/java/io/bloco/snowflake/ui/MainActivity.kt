package io.bloco.snowflake.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.bloco.snowflake.App
import io.bloco.snowflake.background.SnowflakeWorker
import io.bloco.snowflake.ui.theme.SnowflakeTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val dependencies by lazy { (applicationContext as App).dependencies }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SnowflakeTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(
                    values = arrayOf(LocalSnackbarHostState provides snackbarHostState),
                ) {
                    SnowflakeTheme {
                        Scaffold(
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(bottom = it.calculateBottomPadding())
                            ) {
                                Navigation(
                                    navController = navController,
                                    dependencies = dependencies,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Battery Optimization

    private var ignoreBatteryOptimizationCallback: (() -> Unit)? = null

    private val ignoreBatteryOptimizationContract =
        registerForActivityResult(object : ActivityResultContract<Unit, Unit>() {
            @SuppressLint("BatteryLife")
            override fun createIntent(
                context: Context,
                input: Unit,
            ) = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData("package:$packageName".toUri())

            override fun parseResult(
                resultCode: Int,
                intent: Intent?,
            ) {}
        }) { ignoreBatteryOptimizationCallback?.invoke() }

    fun requestIgnoreBatteryOptimization(callback: () -> Unit) {
        ignoreBatteryOptimizationCallback = callback
        ignoreBatteryOptimizationContract.launch(Unit)
    }
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState?> { null }
