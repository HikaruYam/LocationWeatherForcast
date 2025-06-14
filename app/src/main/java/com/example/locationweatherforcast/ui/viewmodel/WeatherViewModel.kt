package com.example.locationweatherforcast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationweatherforcast.data.location.LocationDisabledException
import com.example.locationweatherforcast.data.location.LocationNotFoundException
import com.example.locationweatherforcast.data.model.WeatherData
import com.example.locationweatherforcast.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel for weather forecast screen
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    // Permission state
    private val _permissionState = MutableStateFlow(weatherRepository.hasLocationPermission())
    val permissionState: StateFlow<Boolean> = _permissionState.asStateFlow()
    
    // Performance: Cache last fetch time to avoid rapid successive requests
    private var lastFetchTime = 0L
    private val minimumFetchInterval = 10_000L // 10 seconds
    
    init {
        if (_permissionState.value) {
            // Performance: Use lazy loading on init to speed up app startup
            viewModelScope.launch {
                // Small delay to allow UI to render first
                kotlinx.coroutines.delay(100)
                fetchWeatherForecast()
            }
        } else {
            _uiState.value = WeatherUiState.PermissionRequired
        }
    }
    
    /**
     * Force refresh weather data (ignores cache)
     */
    fun refreshWeatherForecast() {
        fetchWeatherForecast(forceRefresh = true)
    }
    
    /**
     * Clear cached data and cleanup resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            // Cleanup expired cache when ViewModel is destroyed
            try {
                weatherRepository.cleanupCache()
            } catch (e: Exception) {
                // Log error but don't crash
            }
        }
    }
    
    /**
     * Fetch weather forecast data with debouncing to prevent rapid successive requests
     */
    fun fetchWeatherForecast(forceRefresh: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        
        // Performance: Skip request if too recent (unless forced)
        if (!forceRefresh && (currentTime - lastFetchTime) < minimumFetchInterval) {
            return
        }
        
        lastFetchTime = currentTime
        
        // Only show loading if we don't have data or forced refresh
        if (_uiState.value !is WeatherUiState.Success || forceRefresh) {
            _uiState.value = WeatherUiState.Loading
        }
        
        viewModelScope.launch {
            try {
                if (!weatherRepository.hasLocationPermission()) {
                    _uiState.value = WeatherUiState.PermissionRequired
                    return@launch
                }
                
                if (!weatherRepository.isLocationEnabled()) {
                    _uiState.value = WeatherUiState.Error(
                        type = ErrorType.LOCATION_DISABLED,
                        message = "位置情報サービスが無効です。設定から有効にしてください。"
                    )
                    return@launch
                }
                
                val weatherData = weatherRepository.getWeatherForecast()
                _uiState.value = WeatherUiState.Success(weatherData)
            } catch (e: LocationDisabledException) {
                _uiState.value = WeatherUiState.Error(
                    type = ErrorType.LOCATION_DISABLED,
                    message = "位置情報サービスが無効です。設定から有効にしてください。"
                )
            } catch (e: LocationNotFoundException) {
                _uiState.value = WeatherUiState.Error(
                    type = ErrorType.LOCATION_NOT_FOUND,
                    message = "位置情報を取得できませんでした。"
                )
            } catch (e: SecurityException) {
                _uiState.value = WeatherUiState.PermissionRequired
            } catch (e: IOException) {
                _uiState.value = WeatherUiState.Error(
                    type = ErrorType.NETWORK_ERROR,
                    message = "ネットワークエラーが発生しました。インターネット接続を確認してください。"
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(
                    type = ErrorType.GENERIC_ERROR,
                    message = "エラーが発生しました: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Update permission state
     */
    fun updatePermissionState() {
        _permissionState.value = weatherRepository.hasLocationPermission()
        if (_permissionState.value && _uiState.value is WeatherUiState.PermissionRequired) {
            fetchWeatherForecast()
        }
    }
}

/**
 * UI state for weather forecast screen
 */
sealed class WeatherUiState {
    /**
     * Loading state
     */
    object Loading : WeatherUiState()
    
    /**
     * Success state with weather data
     */
    data class Success(val data: WeatherData) : WeatherUiState()
    
    /**
     * Error state with error type and message
     */
    data class Error(
        val type: ErrorType,
        val message: String
    ) : WeatherUiState()
    
    /**
     * Permission required state
     */
    object PermissionRequired : WeatherUiState()
}

/**
 * Error types for better error handling
 */
enum class ErrorType {
    NETWORK_ERROR,
    LOCATION_DISABLED,
    LOCATION_NOT_FOUND,
    API_ERROR,
    GENERIC_ERROR
}
