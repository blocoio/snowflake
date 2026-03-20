package io.bloco.snowflake.ui.home

import android.text.format.Formatter
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
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
    openAbout: () -> Unit,
    openSettings: () -> Unit,
) {
    val isEnabled = state.config?.isEnabled == true
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isShortHeight =
        !sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

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
                    .padding(vertical = 16.dp),
        ) {
            Image(
                painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.app_name),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .align(Alignment.CenterStart),
            )

            Row(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp),
            ) {
                IconButton(
                    onClick = { openAbout() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_about),
                        contentDescription = stringResource(R.string.about),
                    )
                }
                IconButton(
                    onClick = { openSettings() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.settings),
                    )
                }
            }
        }

        Spacer(Modifier.weight(3f))

        Text(
            text =
                stringResource(
                    if (isEnabled) {
                        R.string.snowflake_enabled
                    } else {
                        R.string.snowflake_disabled
                    },
                ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { openAbout() })
                    .padding(horizontal = 16.dp),
        )

        val isEnabled = state.config?.isEnabled == true
        Surface(
            color = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .selectable(
                        selected = isEnabled,
                        role = Role.Switch,
                        onClick = { onEvent(HomeViewModel.Event.EnabledChange(!isEnabled)) },
                    ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        horizontal = 32.dp,
                        vertical = if (isShortHeight) 16.dp else 40.dp,
                    ),
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

        if (!isShortHeight) {
            Spacer(Modifier.weight(2f))

            Image(
                painter = painterResource(R.drawable.home_mobile),
                contentDescription = null,
                colorFilter = if (!isEnabled) {
                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                } else {
                    null
                },
                modifier = Modifier.height(96.dp),
            )
        }

        Spacer(Modifier.weight(2f))

        Stats(state, isShortHeight)

        if (state.isIgnoringBatteryOptimizations == false && !isShortHeight) {
            BatteryOptimizationWarning(requestBatteryOptimization)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Stats(
    state: HomeViewModel.State,
    isShortHeight: Boolean,
) {
    val activity = LocalActivity.current

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        maxItemsInEachRow = if (isShortHeight) 4 else 2,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        val running = state.snowflakeState as? SnowflakeManager.State.Running
        StatsCell(
            title = when {
                running == null -> stringResource(R.string.snowflake_stopped)
                running.clientsConnected > 0 -> stringResource(R.string.snowflake_helping)
                else -> stringResource(R.string.snowflake_looking_to_help)
            },
            text = if (running != null && running.clientsConnected > 0) {
                pluralStringResource(
                    R.plurals.persons,
                    running.clientsConnected,
                    running.clientsConnected,
                )
            } else {
                null
            },
        )
        StatsCell(
            title = stringResource(R.string.snowflake_stats_connections),
            text = state.stats.connections.toString() + " " + stringResource(R.string.today),
        )
        StatsCell(
            title = stringResource(R.string.snowflake_stats_inbound),
            text = Formatter.formatShortFileSize(
                activity,
                state.stats.inboundBytes,
            ) + " " + stringResource(R.string.today),
        )
        StatsCell(
            title = stringResource(R.string.snowflake_stats_outbound),
            text = Formatter.formatShortFileSize(
                activity,
                state.stats.outboundBytes,
            ) + " " + stringResource(R.string.today),
        )
    }
}

@Composable
fun FlowRowScope.StatsCell(
    title: String? = null,
    text: String? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerLowest,
                MaterialTheme.shapes.small,
            ).defaultMinSize(minHeight = 72.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .weight(1f),
    ) {
        title?.let {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = if (text == null) 0.dp else 4.dp),
            )
        }
        text?.let {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
            openAbout = {},
            openSettings = {},
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
            openAbout = {},
            openSettings = {},
        )
    }
}
