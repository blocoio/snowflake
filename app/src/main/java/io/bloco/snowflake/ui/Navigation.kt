package io.bloco.snowflake.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.bloco.snowflake.Dependencies
import io.bloco.snowflake.ui.about.AboutScreen
import io.bloco.snowflake.ui.home.HomeScreen
import io.bloco.snowflake.ui.settings.SettingsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    dependencies: Dependencies,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            val viewModel = viewModel {
                dependencies.homeViewModel(
                    openAbout = { navController.navigate(Screen.About.route) },
                    openSettings = { navController.navigate(Screen.Settings.route) },
                )
            }
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                requestBatteryOptimization = dependencies.batteryOptimization::requestIgnore,
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                goBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel = viewModel {
                dependencies.settingsViewModel(
                    goBack = { navController.popBackStack() }
                )
            }
            val state by viewModel.state.collectAsStateWithLifecycle()
            SettingsScreen(
                state = state,
                onEvent = viewModel::onEvent,
            )
        }
    }
}
