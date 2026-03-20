package io.bloco.snowflake.ui.about

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bloco.snowflake.R
import io.bloco.snowflake.ui.theme.SnowflakeTheme

@Composable
fun AboutScreen(goBack: () -> Unit) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.about)) },
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
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 16.dp),
        ) {
            AboutTitle(R.string.about_title1)
            AboutText(R.string.about_text1)

            AboutTitle(R.string.about_title2)
            AboutText(R.string.about_text2)

            AboutTitle(R.string.about_title3)
            AboutText(R.string.about_text3)

            AboutTitle(R.string.about_title4)
            AboutText(R.string.about_text4)

            AboutTitle(R.string.about_title5)
            AboutText(R.string.about_text5)
        }
    }
}

@Composable
private fun AboutTitle(textRes: Int) {
    SelectionContainer {
        Text(
            stringResource(textRes),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
    }
}

@Composable
private fun AboutText(textRes: Int) {
    val text = stringResource(textRes)
    val ann = remember(text) {
        buildAnnotatedString {
            append(text)
            Patterns.WEB_URL.toRegex().findAll(text).forEach { result ->
                addLink(
                    url = LinkAnnotation.Url(result.value),
                    start = result.range.first,
                    end = result.range.last,
                )
            }
        }
    }
    SelectionContainer {
        Text(
            ann,
            style = MaterialTheme.typography.bodyMedium,
        )
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
