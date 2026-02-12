package io.bloco.snowflake.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bloco.snowflake.R
import io.bloco.snowflake.models.Capacity
import io.bloco.snowflake.ui.theme.SnowflakeTheme

@Composable
fun SettingsScreen(
    state: SettingsViewModel.State,
    onEvent: (SettingsViewModel.Event) -> Unit,
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = { onEvent(SettingsViewModel.Event.BackClick) }) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            val background = state.config?.background == true
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = background,
                        role = Role.Switch,
                        onClick = { onEvent(SettingsViewModel.Event.UnmeteredOnlyChange(!background)) }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_background),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = background,
                    onCheckedChange = null,
                )
            }
            Text(
                text = stringResource(R.string.settings_background_context),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 28.dp)
            )

            val unmeteredOnly = state.config?.unmeteredOnly == true
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = unmeteredOnly,
                        role = Role.Switch,
                        onClick = { onEvent(SettingsViewModel.Event.UnmeteredOnlyChange(!unmeteredOnly)) }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_unmetered),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = unmeteredOnly,
                    onCheckedChange = null,
                )
            }
            Text(
                text = stringResource(R.string.settings_unmetered_context),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 28.dp)
            )

            val chargingOnly = state.config?.chargingOnly == true
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = chargingOnly,
                        role = Role.Switch,
                        onClick = { onEvent(SettingsViewModel.Event.ChargingOnlyChange(!chargingOnly)) }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_charging),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = chargingOnly,
                    onCheckedChange = null,
                )
            }
            Text(
                text = stringResource(R.string.settings_charging_context),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 28.dp)
            )

            Text(
                text = stringResource(R.string.settings_capacity),
                style = MaterialTheme.typography.titleMediumEmphasized,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            Slider(
                value = CAPACITY_RANGE_VALUES.entries
                    .firstOrNull { it.value == state.capacity }
                    ?.key
                    ?: CAPACITY_RANGE_VALUES.keys.first(),
                onValueChange = {
                    val cap = CAPACITY_RANGE_VALUES[it] ?: CAPACITY_RANGE_VALUES.values.first()
                    onEvent(SettingsViewModel.Event.CapacityChange(cap))
                },
                valueRange = CAPACITY_RANGE_VALUES.keys.let { it.first()..it.last() },
                steps = CAPACITY_RANGE_VALUES.size - 2,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
            )
            Text(
                text = when (state.capacity) {
                    is Capacity.Specific ->
                        pluralStringResource(
                            R.plurals.settings_capacity_specific,
                            state.capacity.value.toInt(),
                            state.capacity.value
                        )

                    Capacity.Unlimited -> stringResource(R.string.settings_capacity_unlimited)
                    null -> ""
                },
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

private val CAPACITY_RANGE_VALUES = mapOf(
    1f to Capacity.Specific(1),
    2f to Capacity.Specific(2),
    3f to Capacity.Specific(3),
    4f to Capacity.Specific(4),
    5f to Capacity.Specific(5),
    6f to Capacity.Unlimited,
)

@Composable
@Preview
private fun SettingsScreenPreview() {
    SnowflakeTheme {
        SettingsScreen(
            state = SettingsViewModel.State(
                capacity = Capacity.Specific(2),
            ),
            onEvent = {},
        )
    }
}