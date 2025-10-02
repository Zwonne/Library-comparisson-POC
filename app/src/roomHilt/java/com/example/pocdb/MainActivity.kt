package com.example.pocdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POCDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        upsertRoom = viewModel::upsertRoom,
                        collectAllRoomChannels = viewModel::collectAllRoomChannels,
                        collectRoomChannelsForToday = viewModel::collectRoomChannelsForToday,
                        collectAllRoomPrograms = viewModel::collectAllRoomPrograms,
                        deleteAllRoomEntities = viewModel::deleteAllRoomEntities
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    upsertRoom: () -> Unit,
    collectAllRoomChannels: () -> Unit,
    collectRoomChannelsForToday: () -> Unit,
    collectAllRoomPrograms: () -> Unit,
    deleteAllRoomEntities: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = upsertRoom
            ) {
                Text(text = "Upsert Room")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = collectAllRoomChannels
            ) {
                Text(text = "Collect all room channels")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = collectRoomChannelsForToday
            ) {
                Text(text = "Collect room channels for today")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = collectAllRoomPrograms
            ) {
                Text(text = "Collect all room programs")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = deleteAllRoomEntities
            ) {
                Text(text = "Delete all room entities")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    POCDBTheme {
        Greeting(
            upsertRoom = {},
            collectAllRoomChannels = {},
            collectRoomChannelsForToday = {},
            collectAllRoomPrograms = {},
            deleteAllRoomEntities = {}
        )
    }
}