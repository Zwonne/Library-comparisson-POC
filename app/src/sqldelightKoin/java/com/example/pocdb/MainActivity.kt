package com.example.pocdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawn
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pocdb.ui.theme.POCDBTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POCDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        upsertSqlDelight = viewModel::upsertSqlDelight,
                        collectAllSqlDelightChannels = viewModel::collectAllSqlDelightChannels,
                        collectSqlDelightChannelsForToday = viewModel::collectSqlDelightChannelsForToday,
                        collectAllSqlDelightPrograms = viewModel::collectAllSqlDelightPrograms,
                        deleteAllSqlDelightEntities = viewModel::deleteAllSqlDelightEntities
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    upsertSqlDelight: () -> Unit,
    collectAllSqlDelightChannels: () -> Unit,
    collectSqlDelightChannelsForToday: () -> Unit,
    collectAllSqlDelightPrograms: () -> Unit,
    deleteAllSqlDelightEntities: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = upsertSqlDelight
            ) {
                Text(text = "Upsert SqlDelight")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = collectAllSqlDelightChannels
            ) {
                Text(text = "Collect all SqlDelight channels")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = collectSqlDelightChannelsForToday
            ) {
                Text(text = "Collect SqlDelight channels for today")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = collectAllSqlDelightPrograms
            ) {
                Text(text = "Collect all SqlDelight programs")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = deleteAllSqlDelightEntities
            ) {
                Text(text = "Delete all SqlDelight entities")
            }
        }
    }

    // Should be called after loading of data on the screen is done. But since this is a dummy app, and the UI is static,
    // This will result in "timeToFullDisplayMs" and "timeToInitialDisplayMs" metrics to be identical.
    ReportDrawn()
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    POCDBTheme {
        Greeting(
            upsertSqlDelight = {},
            collectAllSqlDelightChannels = {},
            collectSqlDelightChannelsForToday = {},
            collectAllSqlDelightPrograms = {},
            deleteAllSqlDelightEntities = {}
        )
    }
}