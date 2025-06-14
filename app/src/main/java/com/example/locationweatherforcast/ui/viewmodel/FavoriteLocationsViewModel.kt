package com.example.locationweatherforcast.ui.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.locationweatherforcast.data.database.FavoriteLocation
import com.example.locationweatherforcast.data.location.LocationService
import com.example.locationweatherforcast.data.model.FavoriteLocationWithWeather
import com.example.locationweatherforcast.data.model.WeatherData
import com.example.locationweatherforcast.data.model.LocationData
import com.example.locationweatherforcast.data.repository.WeatherRepository
import com.example.locationweatherforcast.data.repository.FavoriteLocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.UUID

/**
 * ViewModel for favorite locations screen
 */
@HiltViewModel
class FavoriteLocationsViewModel @Inject constructor(
    application: Application,
    private val weatherRepository: WeatherRepository,
    private val favoriteLocationRepository: FavoriteLocationRepository,
    private val locationService: LocationService
) : AndroidViewModel(application) {
    
    // UI state
    private val _uiState = MutableStateFlow<FavoriteLocationsUiState>(FavoriteLocationsUiState.Loading)
    val uiState: StateFlow<FavoriteLocationsUiState> = _uiState.asStateFlow()
    
    // Loading state for individual locations
    private val _loadingLocationIds = MutableStateFlow<Set<String>>(emptySet())
    val loadingLocationIds: StateFlow<Set<String>> = _loadingLocationIds.asStateFlow()
    
    // TODO: Replace with FavoriteLocationRepository when available
    private val mockFavoriteLocations = mutableListOf<FavoriteLocationWithWeather>()
    private val maxLocations = 5
    
    // Performance: Cache last refresh time to avoid rapid successive requests
    private var lastRefreshTime = 0L
    private val minimumRefreshInterval = 30_000L // 30 seconds for bulk refresh
    private val individualRefreshInterval = 10_000L // 10 seconds for individual refresh
    private val refreshTimestamps = mutableMapOf<String, Long>()
    
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
                
                if (mockFavoriteLocations.isEmpty()) {
                    _uiState.value = FavoriteLocationsUiState.Empty
                } else {
                    val canAddMore = mockFavoriteLocations.size < maxLocations
                    _uiState.value = FavoriteLocationsUiState.Success(
                        locations = mockFavoriteLocations.toList(),
                        canAddMore = canAddMore
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.GENERIC_ERROR,
                    message = "お気に入り場所の読み込みに失敗しました: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Add current location to favorites
     */
    fun addCurrentLocation() {
        if (mockFavoriteLocations.size >= maxLocations) {
            _uiState.value = FavoriteLocationsUiState.Error(
                type = FavoriteLocationErrorType.MAX_LOCATIONS_REACHED,
                message = "お気に入り場所は最大${maxLocations}箇所までです"
            )
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
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.PERMISSION_ERROR,
                    message = "位置情報の許可が必要です"
                )
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.GENERIC_ERROR,
                    message = "現在地の追加に失敗しました: ${e.message}"
                )
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
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.GENERIC_ERROR,
                    message = "場所の削除に失敗しました: ${e.message}"
                )
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
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.GENERIC_ERROR,
                    message = "並び替えに失敗しました: ${e.message}"
                )
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
                
                if (mockFavoriteLocations.isEmpty()) {
                    loadFavoriteLocations()
                    return@launch
                }
                
                // Fetch weather data for each location in parallel
                val updatedLocations = mockFavoriteLocations.map { location ->
                    async {
                        try {
                            val weatherData = weatherRepository.getWeatherForecastForLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                forceRefresh = true // Force refresh from API when manually refreshing
                            )
                            location.copy(weatherData = weatherData)
                        } catch (e: Exception) {
                            // Keep the location but with null weather data if fetch fails
                            location.copy(weatherData = null)
                        }
                    }
                }.awaitAll()
                
                // Update the mock data
                mockFavoriteLocations.clear()
                mockFavoriteLocations.addAll(updatedLocations)
                
                // Update UI state
                val canAddMore = mockFavoriteLocations.size < maxLocations
                _uiState.value = FavoriteLocationsUiState.Success(
                    locations = mockFavoriteLocations.toList(),
                    canAddMore = canAddMore
                )
                
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.NETWORK_ERROR,
                    message = "天気データの更新に失敗しました: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Refresh weather data for a specific location
     */
    fun refreshLocationWeatherData(locationId: String) {
        viewModelScope.launch {
            try {
                // Add location to loading set
                _loadingLocationIds.value = _loadingLocationIds.value + locationId
                
                val locationIndex = mockFavoriteLocations.indexOfFirst { it.id == locationId }
                if (locationIndex == -1) {
                    // Remove from loading set if location not found
                    _loadingLocationIds.value = _loadingLocationIds.value - locationId
                    return@launch
                }
                
                val location = mockFavoriteLocations[locationIndex]
                val weatherData = weatherRepository.getWeatherForecastForLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    forceRefresh = true // Force refresh from API when manually refreshing
                )
                
                // Update the specific location
                mockFavoriteLocations[locationIndex] = location.copy(weatherData = weatherData)
                
                // Remove from loading set
                _loadingLocationIds.value = _loadingLocationIds.value - locationId
                
                // Update UI state
                val canAddMore = mockFavoriteLocations.size < maxLocations
                _uiState.value = FavoriteLocationsUiState.Success(
                    locations = mockFavoriteLocations.toList(),
                    canAddMore = canAddMore
                )
                
            } catch (e: Exception) {
                // Remove from loading set on error
                _loadingLocationIds.value = _loadingLocationIds.value - locationId
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.NETWORK_ERROR,
                    message = "天気データの更新に失敗しました: ${e.message}"
                )
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
    
    /**
     * Add a custom location by coordinates
     */
    fun addCustomLocation(name: String, latitude: Double, longitude: Double) {
        if (mockFavoriteLocations.size >= maxLocations) {
            _uiState.value = FavoriteLocationsUiState.Error(
                type = FavoriteLocationErrorType.MAX_LOCATIONS_REACHED,
                message = "お気に入り場所は最大${maxLocations}箇所までです"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = FavoriteLocationsUiState.Loading
                
                // Create location data
                val locationData = LocationData(
                    latitude = latitude,
                    longitude = longitude,
                    name = name
                )
                
                // Get weather data for the specified location
                val weatherData = weatherRepository.getWeatherForecastForLocation(latitude, longitude)
                
                // Create new favorite location
                val newLocation = FavoriteLocationWithWeather(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
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
                
            } catch (e: Exception) {
                _uiState.value = FavoriteLocationsUiState.Error(
                    type = FavoriteLocationErrorType.GENERIC_ERROR,
                    message = "場所の追加に失敗しました: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Clear cached data and cleanup resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            // Performance: Cleanup expired cache and clear refresh timestamps
            try {
                weatherRepository.cleanupCache()
                refreshTimestamps.clear()
            } catch (e: Exception) {
                // Log error but don't crash
            }
        }
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
     * Empty state with no favorite locations
     */
    object Empty : FavoriteLocationsUiState()
    
    /**
     * Error state with error type and message
     */
    data class Error(
        val type: FavoriteLocationErrorType,
        val message: String
    ) : FavoriteLocationsUiState()
}

/**
 * Error types for favorite locations
 */
enum class FavoriteLocationErrorType {
    NETWORK_ERROR,
    PERMISSION_ERROR,
    LOCATION_DISABLED,
    MAX_LOCATIONS_REACHED,
    GENERIC_ERROR
}