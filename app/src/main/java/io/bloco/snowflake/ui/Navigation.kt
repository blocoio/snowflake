package io.bloco.snowflake.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.bloco.snowflake.Dependencies
import io.bloco.snowflake.ui.about.AboutScreen
import io.bloco.snowflake.ui.home.HomeScreen
import io.bloco.snowflake.ui.settings.SettingsScreen
import io.bloco.snowflake.ui.stats.StatsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    dependencies: Dependencies,
    screenBackgroundColor: Color,
) {
    val defaultModifier = Modifier
        .fillMaxSize()
        .background(screenBackgroundColor)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(Screen.Home.route) {
            val viewModel = viewModel { dependencies.homeViewModel() }
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                requestBatteryOptimization = dependencies.batteryOptimization::requestIgnore,
                openAbout = { navController.navigate(Screen.About.route) },
                openSettings = { navController.navigate(Screen.Settings.route) },
                openStats = { navController.navigate(Screen.Stats.route) },
                modifier = defaultModifier,
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                goBack = { navController.popBackStack() },
                modifier = defaultModifier,
            )
        }

        composable(Screen.Settings.route) {
            val viewModel = viewModel { dependencies.settingsViewModel() }
            val state by viewModel.state.collectAsStateWithLifecycle()
            SettingsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                goBack = { navController.popBackStack() },
                modifier = defaultModifier,
            )
        }

        composable(Screen.Stats.route) {
            val viewModel = viewModel { dependencies.statsViewModel() }
            val state by viewModel.state.collectAsStateWithLifecycle()
            StatsScreen(
                state = state,
                goBack = { navController.popBackStack() },
            )
        }
    }
}
