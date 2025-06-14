package com.example.locationweatherforcast.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.locationweatherforcast.ui.components.*
import com.example.locationweatherforcast.ui.viewmodel.WeatherUiState
import com.example.locationweatherforcast.ui.viewmodel.WeatherViewModel
import com.example.locationweatherforcast.ui.viewmodel.ErrorType

/**
 * Main weather forecast screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (locationPermissionGranted) {
            viewModel.updatePermissionState()
        }
    }
    
    // Request permissions function - memoized to prevent recomposition
    val requestPermissions = remember {
        {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    // Refresh action - memoized to prevent recomposition
    val refreshAction = remember(viewModel) {
        { viewModel.fetchWeatherForecast() }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("明日の天気予報") },
                actions = {
                    IconButton(onClick = refreshAction) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "更新"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display different UI based on state
            when (uiState) {
                is WeatherUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WeatherCardSkeleton(
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                
                is WeatherUiState.Success -> {
                    val weatherData = (uiState as WeatherUiState.Success).data
                    
                    // Memoized weather content to prevent unnecessary recomposition
                    val weatherContent = remember(weatherData) {
                        weatherData
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WeatherCard(
                            weatherData = weatherContent,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                
                is WeatherUiState.Error -> {
                    val errorState = uiState as WeatherUiState.Error
                    
                    when (errorState.type) {
                        ErrorType.NETWORK_ERROR -> {
                            NetworkErrorComponent(
                                onRetry = refreshAction
                            )
                        }
                        ErrorType.LOCATION_DISABLED -> {
                            GpsDisabledErrorComponent(
                                onEnableGps = {
                                    // TODO: Open GPS settings
                                },
                                onManualLocation = {
                                    // TODO: Add manual location input
                                }
                            )
                        }
                        ErrorType.LOCATION_NOT_FOUND -> {
                            ErrorStateComponent(
                                title = "位置情報が見つかりません",
                                message = errorState.message,
                                primaryAction = ErrorAction(
                                    text = "再試行",
                                    icon = Icons.Default.Refresh,
                                    onClick = refreshAction
                                )
                            )
                        }
                        ErrorType.API_ERROR -> {
                            ApiErrorComponent(
                                message = errorState.message,
                                onRetry = refreshAction
                            )
                        }
                        ErrorType.GENERIC_ERROR -> {
                            ErrorStateComponent(
                                title = "エラーが発生しました",
                                message = errorState.message,
                                primaryAction = ErrorAction(
                                    text = "再試行",
                                    icon = Icons.Default.Refresh,
                                    onClick = refreshAction
                                )
                            )
                        }
                    }
                }
                
                is WeatherUiState.PermissionRequired -> {
                    LocationPermissionErrorComponent(
                        onRequestPermission = requestPermissions,
                        onManualLocation = {
                            // TODO: Add manual location input
                        }
                    )
                }
            }
        }
    }
}
