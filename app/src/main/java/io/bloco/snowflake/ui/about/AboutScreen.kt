package io.bloco.snowflake.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bloco.snowflake.R
import io.bloco.snowflake.ui.theme.SnowflakeTheme

@Composable
fun AboutScreen(
    goBack: () -> Unit,
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.about)) },
            navigationIcon = {
                IconButton(onClick = goBack) {
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
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.about_text))
        }
    }
}

@Composable
@Preview
private fun AboutScreenPreview() {
    SnowflakeTheme {
        AboutScreen(
            goBack = {},
        )
    }
}
