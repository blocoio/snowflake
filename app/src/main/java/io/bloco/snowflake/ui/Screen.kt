package io.bloco.snowflake.ui

sealed class Screen(
    val route: String,
) {
    data object Home : Screen("home")
    data object About : Screen("about")
    data object Settings : Screen("settings")
}
