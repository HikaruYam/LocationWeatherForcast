package com.example.locationweatherforcast.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationweatherforcast.data.model.FavoriteLocationWithWeather
import com.example.locationweatherforcast.ui.components.WeatherIcon
import com.example.locationweatherforcast.ui.components.LocationCard
import com.example.locationweatherforcast.ui.components.LocationInputDialog
import com.example.locationweatherforcast.ui.components.QuickLocationDialog
import com.example.locationweatherforcast.ui.viewmodel.FavoriteLocationsViewModel
import com.example.locationweatherforcast.ui.viewmodel.FavoriteLocationsUiState

@Composable
fun FavoriteLocationsScreen(
    viewModel: FavoriteLocationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLocationInputDialog by remember { mutableStateOf(false) }
    var showQuickLocationDialog by remember { mutableStateOf(false) }
    var showAddMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "お気に入りの場所",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { viewModel.refreshWeatherData() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "天気データを更新"
                    )
                }
            }
        },
        floatingActionButton = {
            when (val state = uiState) {
                is FavoriteLocationsUiState.Success -> {
                    if (state.canAddMore) {
                        FloatingActionButton(
                            onClick = { showAddMenu = true },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "場所を追加"
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is FavoriteLocationsUiState.Loading -> {
                    LoadingContent()
                }
                is FavoriteLocationsUiState.Success -> {
                    if (state.locations.isEmpty()) {
                        EmptyContent(
                            onAddCurrentLocation = { viewModel.addCurrentLocation() }
                        )
                    } else {
                        LocationsList(
                            locations = state.locations,
                            onDeleteLocation = { locationId ->
                                viewModel.deleteLocation(locationId)
                            }
                        )
                    }
                }
                is FavoriteLocationsUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.refreshWeatherData() }
                    )
                }
            }
        }
    }
    
    // Add location options menu
    DropdownMenu(
        expanded = showAddMenu,
        onDismissRequest = { showAddMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("現在地を追加") },
            onClick = {
                showAddMenu = false
                viewModel.addCurrentLocation()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = { Text("人気の場所から選択") },
            onClick = {
                showAddMenu = false
                showQuickLocationDialog = true
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = { Text("手動で追加") },
            onClick = {
                showAddMenu = false
                showLocationInputDialog = true
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
        )
    }
    
    // Location input dialog
    if (showLocationInputDialog) {
        LocationInputDialog(
            onDismiss = { showLocationInputDialog = false },
            onConfirm = { name, latitude, longitude ->
                showLocationInputDialog = false
                viewModel.addCustomLocation(name, latitude, longitude)
            }
        )
    }
    
    // Quick location selection dialog
    if (showQuickLocationDialog) {
        QuickLocationDialog(
            onDismiss = { showQuickLocationDialog = false },
            onSelectLocation = { name, latitude, longitude ->
                showQuickLocationDialog = false
                viewModel.addCustomLocation(name, latitude, longitude)
            }
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "天気データを読み込み中...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyContent(
    onAddCurrentLocation: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "お気に入りの場所がありません",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "右下の「+」ボタンで現在地を追加できます",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "エラーが発生しました",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = onRetry
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "再試行"
                )
            }
        }
    }
}

@Composable
private fun LocationsList(
    locations: List<FavoriteLocationWithWeather>,
    onDeleteLocation: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations) { location ->
            LocationCard(
                location = location,
                onDeleteClick = { onDeleteLocation(location.id) },
                onCardClick = {
                    // TODO: Navigate to location detail screen
                }
            )
        }
        
        // Add bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

