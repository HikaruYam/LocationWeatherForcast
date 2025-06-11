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
    
    // Request permissions function
    val requestPermissions = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("明日の天気予報") },
                actions = {
                    IconButton(onClick = { viewModel.fetchWeatherForecast() }) {
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
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WeatherCard(
                            weatherData = weatherData,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                
                is WeatherUiState.Error -> {
                    val errorMessage = (uiState as WeatherUiState.Error).message
                    
                    ErrorComponent(
                        message = errorMessage,
                        onRetry = { viewModel.fetchWeatherForecast() }
                    )
                }
                
                is WeatherUiState.PermissionRequired -> {
                    PermissionRequestComponent(
                        onRequestPermission = requestPermissions
                    )
                }
            }
        }
    }
}
