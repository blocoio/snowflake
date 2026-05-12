package io.bloco.snowflake.ui

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager

sealed interface Launcher

object DefaultLauncher : Launcher

object HiddenLauncher : Launcher

val LAUNCHERS = listOf(DefaultLauncher, HiddenLauncher)

fun Activity.setLauncher(launcher: Launcher) {
    LAUNCHERS.forEach {
        setLauncherEnabled(launcher = it, isEnabled = launcher == it)
    }
}

private fun Activity.setLauncherEnabled(
    launcher: Launcher,
    isEnabled: Boolean,
) {
    application.packageManager.setComponentEnabledSetting(
        ComponentName(
            application,
            launcher::class.java.name,
        ),
        if (isEnabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        },
        PackageManager.DONT_KILL_APP,
    )
}
