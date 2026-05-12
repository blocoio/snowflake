package io.bloco.snowflake.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import info.guardianproject.panic.PanicResponder
import io.bloco.snowflake.App
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

/*
 * If a Panic button is trigger, reset the app state as much as possible
 */
class PanicResponseActivity : ComponentActivity() {
    private val dependencies by lazy { (applicationContext as App).dependencies }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PanicResponder.shouldUseDefaultResponseToTrigger(this)) {
            runBlocking {
                dependencies.appDataStore.clear()
                dependencies.appDatabase.clear()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                revokeSelfPermissionOnKill(Manifest.permission.POST_NOTIFICATIONS)
            }
            setLauncher(HiddenLauncher)
            exitProcess(0)
        } else {
            finish()
        }
    }
}
