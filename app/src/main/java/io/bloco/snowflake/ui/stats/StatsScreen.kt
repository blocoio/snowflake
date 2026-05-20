package io.bloco.snowflake.ui.stats

import android.text.format.Formatter
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.bloco.snowflake.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatsScreen(
    state: StatsViewModel.State,
    goBack: () -> Unit,
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.stats)) },
            navigationIcon = {
                IconButton(onClick = goBack) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.close),
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        )

        // Table Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
        ) {
            StatsCell("", isLabel = true)
            StatsCell(stringResource(R.string.snowflake_stats_connections))
            StatsCell(stringResource(R.string.snowflake_stats_inbound))
            StatsCell(stringResource(R.string.snowflake_stats_outbound))
        }
        TableDivider(Modifier.padding(horizontal = 16.dp))

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 12.dp,
                bottom = 32.dp +
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            // Days
            items(state.days, key = { it.date }) {
                Row {
                    StatsCell(
                        if (it.date == LocalDate.now()) {
                            stringResource(R.string.today)
                        } else {
                            it.date.format(DAY_FORMAT)
                        },
                        isLabel = true,
                    )
                    StatsCell(it.connections.toString())
                    StatsCell(it.inboundBytes.humanBytes())
                    StatsCell(it.outboundBytes.humanBytes())
                }
                TableDivider()
            }

            // Months
            if (state.months.size > 1) {
                items(state.months, key = { "month_${it.date}" }) {
                    Row {
                        StatsCell(it.date.format(MONTH_FORMAT), isLabel = true)
                        StatsCell(it.connections.toString())
                        StatsCell(it.inboundBytes.humanBytes())
                        StatsCell(it.outboundBytes.humanBytes())
                    }
                    TableDivider()
                }
            }

            // Years
            if (state.years.size > 1) {
                items(state.years, key = { "year_${it.date.year}" }) {
                    Row {
                        StatsCell(it.date.year.toString(), isLabel = true)
                        StatsCell(it.connections.toString())
                        StatsCell(it.inboundBytes.humanBytes())
                        StatsCell(it.outboundBytes.humanBytes())
                    }
                    TableDivider()
                }
            }

            // Total
            state.total?.let { total ->
                item("total") {
                    Row {
                        StatsCell(stringResource(R.string.stats_total), isLabel = true)
                        StatsCell(total.connections.toString())
                        StatsCell(total.inboundBytes.humanBytes())
                        StatsCell(total.outboundBytes.humanBytes())
                    }
                    TableDivider()
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatsCell(
    text: String,
    isLabel: Boolean = false,
) {
    Text(
        text = text,
        textAlign = if (isLabel) TextAlign.Start else TextAlign.End,
        fontSize = 14.sp,
        fontWeight = if (isLabel) FontWeight.Bold else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 6.dp)
            .padding(end = 4.dp),
    )
}

@Composable
private fun TableDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(thickness = Dp.Hairline, modifier = modifier)
}

@Composable
private fun Long.humanBytes() =
    Formatter.formatShortFileSize(
        LocalActivity.current,
        this,
    )

private val DAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM")
private val MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy")
