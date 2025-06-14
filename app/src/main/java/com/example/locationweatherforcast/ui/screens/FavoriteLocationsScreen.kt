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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.locationweatherforcast.data.model.FavoriteLocationWithWeather
import com.example.locationweatherforcast.ui.components.WeatherIcon
import com.example.locationweatherforcast.ui.components.LocationCard
import com.example.locationweatherforcast.ui.components.FavoriteLocationsScreenSkeleton
import com.example.locationweatherforcast.ui.components.LocationCardSkeleton
import com.example.locationweatherforcast.ui.components.LoadingIndicator
import com.example.locationweatherforcast.ui.components.ProgressButton
import com.example.locationweatherforcast.ui.components.LocationInputDialog
import com.example.locationweatherforcast.ui.components.QuickLocationDialog
import com.example.locationweatherforcast.ui.components.rememberDragDropState
import com.example.locationweatherforcast.ui.components.dragContainer
import com.example.locationweatherforcast.ui.components.draggedItem
import com.example.locationweatherforcast.ui.viewmodel.FavoriteLocationsViewModel
import com.example.locationweatherforcast.ui.viewmodel.FavoriteLocationsUiState
import com.example.locationweatherforcast.ui.viewmodel.FavoriteLocationErrorType
import com.example.locationweatherforcast.ui.components.EmptyFavoritesComponent
import com.example.locationweatherforcast.ui.components.ErrorStateComponent
import com.example.locationweatherforcast.ui.components.ErrorAction
import com.example.locationweatherforcast.ui.components.NetworkErrorComponent
import com.example.locationweatherforcast.ui.components.LocationPermissionErrorComponent

@Composable
fun FavoriteLocationsScreen(
    viewModel: FavoriteLocationsViewModel = hiltViewModel(),
    onNavigateToLocationDetail: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadingLocationIds by viewModel.loadingLocationIds.collectAsState()
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
                is FavoriteLocationsUiState.Empty -> {
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
                is FavoriteLocationsUiState.Empty -> {
                    EmptyFavoritesComponent(
                        onAddCurrentLocation = { viewModel.addCurrentLocation() },
                        onAddManualLocation = { showLocationInputDialog = true }
                    )
                }
                is FavoriteLocationsUiState.Success -> {
                    LocationsList(
                        locations = state.locations,
                        loadingLocationIds = loadingLocationIds,
                        onDeleteLocation = { locationId ->
                            viewModel.deleteLocation(locationId)
                        },
                        onReorderLocations = { reorderedList ->
                            viewModel.reorderLocations(reorderedList)
                        },
                        onLocationClick = { locationId ->
                            onNavigateToLocationDetail(locationId)
                        },
                        onRefreshLocation = { locationId ->
                            viewModel.refreshLocationWeatherData(locationId)
                        }
                    )
                }
                is FavoriteLocationsUiState.Error -> {
                    when (state.type) {
                        FavoriteLocationErrorType.NETWORK_ERROR -> {
                            NetworkErrorComponent(
                                onRetry = { viewModel.refreshWeatherData() }
                            )
                        }
                        FavoriteLocationErrorType.PERMISSION_ERROR -> {
                            LocationPermissionErrorComponent(
                                onRequestPermission = {
                                    // TODO: Handle permission request
                                },
                                onManualLocation = {
                                    showLocationInputDialog = true
                                }
                            )
                        }
                        FavoriteLocationErrorType.LOCATION_DISABLED -> {
                            ErrorStateComponent(
                                title = "位置情報が無効です",
                                message = state.message,
                                icon = Icons.Default.LocationOn,
                                primaryAction = ErrorAction(
                                    text = "設定を開く",
                                    icon = Icons.Default.LocationOn,
                                    onClick = {
                                        // TODO: Open location settings
                                    }
                                ),
                                secondaryAction = ErrorAction(
                                    text = "手動で追加",
                                    icon = Icons.Default.Edit,
                                    onClick = {
                                        showLocationInputDialog = true
                                    }
                                )
                            )
                        }
                        FavoriteLocationErrorType.MAX_LOCATIONS_REACHED -> {
                            ErrorStateComponent(
                                title = "お気に入りが上限に達しています",
                                message = state.message,
                                icon = Icons.Default.LocationOn,
                                primaryAction = ErrorAction(
                                    text = "場所を削除してから追加",
                                    icon = Icons.Default.Refresh,
                                    onClick = {
                                        // Do nothing, just close error state
                                        viewModel.refreshWeatherData()
                                    }
                                )
                            )
                        }
                        FavoriteLocationErrorType.GENERIC_ERROR -> {
                            ErrorStateComponent(
                                title = "エラーが発生しました",
                                message = state.message,
                                primaryAction = ErrorAction(
                                    text = "再試行",
                                    icon = Icons.Default.Refresh,
                                    onClick = { viewModel.refreshWeatherData() }
                                )
                            )
                        }
                    }
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
    FavoriteLocationsScreenSkeleton()
}


@Composable
private fun LocationsList(
    locations: List<FavoriteLocationWithWeather>,
    loadingLocationIds: Set<String>,
    onDeleteLocation: (String) -> Unit,
    onReorderLocations: (List<FavoriteLocationWithWeather>) -> Unit,
    onLocationClick: (String) -> Unit,
    onRefreshLocation: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(
        lazyListState = listState,
        onMove = { fromIndex, toIndex ->
            val mutableList = locations.toMutableList()
            val item = mutableList.removeAt(fromIndex)
            mutableList.add(toIndex, item)
            onReorderLocations(mutableList)
        }
    )

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .dragContainer(dragDropState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = locations,
            key = { _, location -> location.id }
        ) { index, location ->
            LocationCard(
                location = location,
                onDeleteClick = { onDeleteLocation(location.id) },
                onCardClick = {
                    onLocationClick(location.id)
                },
                onRefreshClick = {
                    onRefreshLocation(location.id)
                },
                isDragEnabled = true,
                isLoading = loadingLocationIds.contains(location.id),
                modifier = Modifier.draggedItem(dragDropState, index)
            )
        }
        
        // Add bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

