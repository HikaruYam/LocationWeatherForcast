package com.example.locationweatherforcast.ui.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import com.example.locationweatherforcast.data.location.LocationService
import com.example.locationweatherforcast.data.model.FavoriteLocationWithWeather
import com.example.locationweatherforcast.data.model.WeatherData
import com.example.locationweatherforcast.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for favorite locations screen
 */
class FavoriteLocationsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val locationService = LocationService(application)
    private val weatherRepository = WeatherRepository(locationService)
    
    // UI state
    private val _uiState = MutableStateFlow<FavoriteLocationsUiState>(FavoriteLocationsUiState.Loading)
    val uiState: StateFlow<FavoriteLocationsUiState> = _uiState.asStateFlow()
    
    // TODO: Replace with FavoriteLocationRepository when available
    private val mockFavoriteLocations = mutableListOf<FavoriteLocationWithWeather>()
    private val maxLocations = 5
    
    init {
        loadFavoriteLocations()
    }
    
    /**
     * Load favorite locations with weather data
     */
    private fun loadFavoriteLocations() {
        _uiState.value = FavoriteLocationsUiState.Loading
        
        viewModelScope.launch {
            try {
                // TODO: Replace with actual repository call when available
                loadMockData()
                
                val canAddMore = mockFavoriteLocations.size < maxLocations
                _uiState.value = FavoriteLocationsUiState.Success(
                    locations = mockFavoriteLocations.toList(),
                    canAddMore = canAddMore
                )
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error("お気に入り場所の読み込みに失敗しました: ${e.message}")
            }
        }
    }
    
    /**
     * Add current location to favorites
     */
    fun addCurrentLocation() {
        if (mockFavoriteLocations.size >= maxLocations) {
            _uiState.value = FavoriteLocationsUiState.Error("お気に入り場所は最大${maxLocations}箇所までです")
            return
        }
        
        viewModelScope.launch {
            try {
                // Get current location
                val currentLocation = locationService.getCurrentLocation()
                
                // Get weather data for current location
                val weatherData = weatherRepository.getWeatherForecast()
                
                // Create new favorite location
                val newLocation = FavoriteLocationWithWeather(
                    id = UUID.randomUUID().toString(),
                    name = "現在地", // TODO: Get actual location name via reverse geocoding
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude,
                    order = mockFavoriteLocations.size,
                    weatherData = weatherData
                )
                
                // TODO: Replace with repository call
                mockFavoriteLocations.add(newLocation)
                
                // Update UI state
                val canAddMore = mockFavoriteLocations.size < maxLocations
                _uiState.value = FavoriteLocationsUiState.Success(
                    locations = mockFavoriteLocations.toList(),
                    canAddMore = canAddMore
                )
                
            } catch (e: SecurityException) {
                _uiState.value = FavoriteLocationsUiState.Error("位置情報の許可が必要です")
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error("現在地の追加に失敗しました: ${e.message}")
            }
        }
    }
    
    /**
     * Delete a favorite location
     */
    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            try {
                // TODO: Replace with repository call
                mockFavoriteLocations.removeAll { it.id == locationId }
                
                // Reorder remaining locations
                mockFavoriteLocations.forEachIndexed { index, location ->
                    mockFavoriteLocations[index] = location.copy(order = index)
                }
                
                // Update UI state
                val canAddMore = mockFavoriteLocations.size < maxLocations
                _uiState.value = FavoriteLocationsUiState.Success(
                    locations = mockFavoriteLocations.toList(),
                    canAddMore = canAddMore
                )
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error("場所の削除に失敗しました: ${e.message}")
            }
        }
    }
    
    /**
     * Reorder favorite locations
     */
    fun reorderLocations(reorderedLocations: List<FavoriteLocationWithWeather>) {
        viewModelScope.launch {
            try {
                // TODO: Replace with repository call
                mockFavoriteLocations.clear()
                reorderedLocations.forEachIndexed { index, location ->
                    mockFavoriteLocations.add(location.copy(order = index))
                }
                
                // Update UI state
                val canAddMore = mockFavoriteLocations.size < maxLocations
                _uiState.value = FavoriteLocationsUiState.Success(
                    locations = mockFavoriteLocations.toList(),
                    canAddMore = canAddMore
                )
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error("並び替えに失敗しました: ${e.message}")
            }
        }
    }
    
    /**
     * Refresh weather data for all locations
     */
    fun refreshWeatherData() {
        viewModelScope.launch {
            try {
                _uiState.value = FavoriteLocationsUiState.Loading
                
                // TODO: Fetch weather data for each location from repository
                loadFavoriteLocations()
                
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error("天気データの更新に失敗しました: ${e.message}")
            }
        }
    }
    
    /**
     * Load mock data for testing
     */
    private fun loadMockData() {
        // Add some mock locations for testing
        mockFavoriteLocations.clear()
        // This will start empty - locations will be added by user actions
    }
    
    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        return weatherRepository.hasLocationPermission()
    }
    
    /**
     * Check if location services are enabled
     */
    fun isLocationEnabled(): Boolean {
        return weatherRepository.isLocationEnabled()
    }
}

/**
 * UI state for favorite locations screen
 */
sealed class FavoriteLocationsUiState {
    /**
     * Loading state
     */
    object Loading : FavoriteLocationsUiState()
    
    /**
     * Success state with locations data
     */
    data class Success(
        val locations: List<FavoriteLocationWithWeather>,
        val canAddMore: Boolean
    ) : FavoriteLocationsUiState()
    
    /**
     * Error state with error message
     */
    data class Error(val message: String) : FavoriteLocationsUiState()
}