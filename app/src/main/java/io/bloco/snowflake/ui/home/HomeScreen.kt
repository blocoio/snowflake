package io.bloco.snowflake.ui.home

import android.text.format.Formatter
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bloco.snowflake.R
import io.bloco.snowflake.background.SnowflakeManager
import io.bloco.snowflake.models.AppConfig
import io.bloco.snowflake.models.DayStats
import io.bloco.snowflake.ui.MainActivity
import io.bloco.snowflake.ui.theme.SnowflakeTheme

@Composable
fun HomeScreen(
    state: HomeViewModel.State,
    onEvent: (HomeViewModel.Event) -> Unit,
    requestBatteryOptimization: (MainActivity) -> Unit,
) {
    val activity = LocalActivity.current
    val isEnabled = state.config?.isEnabled == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 64.dp),
        ) {
            Image(
                painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.app_name),
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .align(Alignment.CenterStart),
            )

            IconButton(
                onClick = { onEvent(HomeViewModel.Event.SettingsClick) },
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.settings),
                )
            }
        }

        Text(
            text =
                stringResource(
                    if (isEnabled) {
                        R.string.snowflake_enabled
                    } else {
                        R.string.snowflake_disabled
                    },
                ),
            style = MaterialTheme.typography.headlineSmall,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        )

        val isEnabled = state.config?.isEnabled == true
        Surface(
            color = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .selectable(
                        selected = isEnabled,
                        role = Role.Switch,
                        onClick = { onEvent(HomeViewModel.Event.EnabledChange(!isEnabled)) },
                    ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp),
            ) {
                Text(
                    text = stringResource(if (isEnabled) R.string.enabled else R.string.disabled),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = null,
                    thumbContent = {
                        if (isEnabled) {
                            Icon(
                                painterResource(R.drawable.ic_check),
                                contentDescription = null,
                            )
                        }
                    },
                    modifier = Modifier.scale(1.5f),
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text =
                stringResource(
                    when (state.snowflakeState) {
                        is SnowflakeManager.State.Running -> R.string.snowflake_running
                        SnowflakeManager.State.Stopped -> R.string.snowflake_stopped
                    },
                ),
            style = MaterialTheme.typography.labelLarge,
        )

        if (state.snowflakeState is SnowflakeManager.State.Running) {
            Text(
                text =
                    if (state.snowflakeState.clientsConnected == 0) {
                        stringResource(R.string.snowflake_looking_to_help)
                    } else {
                        pluralStringResource(
                            R.plurals.snowflake_helping,
                            state.snowflakeState.clientsConnected,
                            state.snowflakeState.clientsConnected,
                        )
                    },
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Text(
            text = stringResource(R.string.snowflake_stats_title),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text =
                stringResource(
                    R.string.snowflake_stats_connections,
                    state.stats.connections,
                ),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text =
                stringResource(
                    R.string.snowflake_stats_inbound,
                    Formatter.formatShortFileSize(activity, state.stats.inboundBytes),
                ),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text =
                stringResource(
                    R.string.snowflake_stats_outbound,
                    Formatter.formatShortFileSize(activity, state.stats.outboundBytes),
                ),
            style = MaterialTheme.typography.labelMedium,
        )

        if (state.isIgnoringBatteryOptimizations == false) {
            BatteryOptimizationWarning(requestBatteryOptimization)
        }

        Spacer(Modifier.weight(1f))

        TextButton(
            onClick = { onEvent(HomeViewModel.Event.AboutClick) },
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.snowflake_learn_more),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}

@Composable
private fun BatteryOptimizationWarning(requestBatteryOptimization: (MainActivity) -> Unit) {
    val activity = LocalActivity.current

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.ic_battery),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp),
                )
                Text(
                    text = stringResource(R.string.battery_optimizations_text),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            TextButton(
                onClick = {
                    (activity as? MainActivity)?.let(requestBatteryOptimization::invoke)
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.battery_optimizations_action))
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    SnowflakeTheme {
        HomeScreen(
            state =
                HomeViewModel.State(
                    snowflakeState = SnowflakeManager.State.Running(2),
                    config =
                        AppConfig(
                            isEnabled = true,
                            background = true,
                            unmeteredOnly = false,
                            chargingOnly = false,
                        ),
                    isIgnoringBatteryOptimizations = false,
                    stats =
                        DayStats(
                            connections = 3,
                            inboundBytes = 1234,
                            outboundBytes = 653682,
                        ),
                ),
            onEvent = {},
            requestBatteryOptimization = {},
        )
    }
}

@Composable
@Preview
private fun HomeScreenDisabledPreview() {
    SnowflakeTheme {
        HomeScreen(
            state =
                HomeViewModel.State(
                    snowflakeState = SnowflakeManager.State.Stopped,
                    config =
                        AppConfig(
                            isEnabled = false,
                            background = true,
                            unmeteredOnly = false,
                            chargingOnly = false,
                        ),
                    isIgnoringBatteryOptimizations = true,
                ),
            onEvent = {},
            requestBatteryOptimization = {},
        )
    }
}
