package com.example.locationweatherforcast.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.locationweatherforcast.ui.screens.WeatherScreen
import com.example.locationweatherforcast.ui.screens.FavoriteLocationsScreen
import com.example.locationweatherforcast.ui.screens.LocationDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.CURRENT_WEATHER
    ) {
        composable(NavigationRoutes.CURRENT_WEATHER) {
            WeatherScreen()
        }
        
        composable(NavigationRoutes.FAVORITE_LOCATIONS) {
            FavoriteLocationsScreen(
                onNavigateToLocationDetail = { locationId ->
                    navController.navigate(NavigationRoutes.locationDetail(locationId))
                }
            )
        }
        
        composable(NavigationRoutes.LOCATION_DETAIL) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
            LocationDetailScreen(
                locationId = locationId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDeleteLocation = {
                    // Navigate back to favorites after deletion
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    // This is a temporary placeholder screen
    // Will be replaced with actual screens in future issues
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}