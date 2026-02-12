package io.bloco.snowflake.ui.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.bloco.snowflake.R
import io.bloco.snowflake.background.SnowflakeManager
import io.bloco.snowflake.models.AppConfig
import io.bloco.snowflake.ui.MainActivity
import io.bloco.snowflake.ui.theme.SnowflakeTheme

@Composable
fun HomeScreen(
    state: HomeViewModel.State,
    onEvent: (HomeViewModel.Event) -> Unit,
    requestBatteryOptimization: (MainActivity) -> Unit,
) {
    val activity = LocalActivity.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.displayLargeEmphasized,
                autoSize = TextAutoSize.StepBased(maxFontSize = 100.sp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 80.dp, bottom = 32.dp),
            )

            IconButton(
                onClick = { onEvent(HomeViewModel.Event.SettingsClick) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.settings),
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        Spacer(Modifier.weight(1f))

        state.config?.let { config ->
            Text(
                text = stringResource(
                    if (config.isEnabled) {
                        R.string.snowflake_enabled
                    } else {
                        R.string.snowflake_disabled
                    }
                ),
                style = if (config.isEnabled) {
                    MaterialTheme.typography.headlineSmallEmphasized
                } else {
                    MaterialTheme.typography.headlineSmall
                },
            )
        }

        val isEnabled = state.config?.isEnabled == true
        Switch(
            checked = isEnabled,
            onCheckedChange = { onEvent(HomeViewModel.Event.EnabledChange(it)) },
            thumbContent = {
                if (isEnabled) {
                    Icon(
                        painterResource(R.drawable.ic_check),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .scale(2f)
                .padding(vertical = 24.dp)
        )

        Text(
            text = stringResource(
                when (state.snowflakeState) {
                    is SnowflakeManager.State.Running -> R.string.snowflake_running
                    SnowflakeManager.State.Stopped -> R.string.snowflake_stopped
                }
            ),
            style = MaterialTheme.typography.labelLarge
        )

        if (state.snowflakeState is SnowflakeManager.State.Running && state.snowflakeState.clientConnected) {
            Text(
                text = "You are currently helping someone",
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (state.isIgnoringBatteryOptimizations == false) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp, bottom = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.battery_optimizations_text),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(
                        onClick = {
                            (activity as? MainActivity)?.let(requestBatteryOptimization::invoke)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.battery_optimizations_action))
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        TextButton(
            onClick = { onEvent(HomeViewModel.Event.AboutClick) },
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.snowflake_learn_more),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    SnowflakeTheme {
        HomeScreen(
            state = HomeViewModel.State(
                snowflakeState = SnowflakeManager.State.Running(true),
                config = AppConfig(
                    isEnabled = true,
                    background = true,
                    unmeteredOnly = false,
                    chargingOnly = false
                ),
                isIgnoringBatteryOptimizations = false,
            ),
            onEvent = {},
            requestBatteryOptimization = {}
        )
    }
}