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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import io.bloco.snowflake.App
import io.bloco.snowflake.ui.theme.SnowflakeTheme
import io.bloco.snowflake.ui.theme.appBackground

class MainActivity : ComponentActivity() {
    private val dependencies by lazy { (applicationContext as App).dependencies }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel = viewModel { dependencies.mainViewModel }
            val state by viewModel.state.collectAsStateWithLifecycle()

            SnowflakeTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(
                    values = arrayOf(LocalSnackbarHostState provides snackbarHostState),
                ) {
                    SnowflakeTheme {
                        Scaffold(
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            containerColor = MaterialTheme.colorScheme.appBackground(state.isEnabled),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = it.calculateStartPadding(LocalLayoutDirection.current),
                                        end = it.calculateEndPadding(LocalLayoutDirection.current),
                                    ),
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
        registerForActivityResult(
            object : ActivityResultContract<Unit, Unit>() {
                @SuppressLint("BatteryLife")
                override fun createIntent(
                    context: Context,
                    input: Unit,
                ) = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData("package:$packageName".toUri())

                override fun parseResult(
                    resultCode: Int,
                    intent: Intent?,
                ) {
                }
            },
        ) { ignoreBatteryOptimizationCallback?.invoke() }

    fun requestIgnoreBatteryOptimization(callback: () -> Unit) {
        ignoreBatteryOptimizationCallback = callback
        ignoreBatteryOptimizationContract.launch(Unit)
    }
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState?> { null }
