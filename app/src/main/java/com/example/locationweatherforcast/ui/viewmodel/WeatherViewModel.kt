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
    
    init {
        if (_permissionState.value) {
            fetchWeatherForecast()
        } else {
            _uiState.value = WeatherUiState.PermissionRequired
        }
    }
    
    /**
     * Fetch weather forecast data
     */
    fun fetchWeatherForecast() {
        _uiState.value = WeatherUiState.Loading
        
        viewModelScope.launch {
            try {
                if (!weatherRepository.hasLocationPermission()) {
                    _uiState.value = WeatherUiState.PermissionRequired
                    return@launch
                }
                
                if (!weatherRepository.isLocationEnabled()) {
                    _uiState.value = WeatherUiState.Error("位置情報サービスが無効です。設定から有効にしてください。")
                    return@launch
                }
                
                val weatherData = weatherRepository.getWeatherForecast()
                _uiState.value = WeatherUiState.Success(weatherData)
            } catch (e: LocationDisabledException) {
                _uiState.value = WeatherUiState.Error("位置情報サービスが無効です。設定から有効にしてください。")
            } catch (e: LocationNotFoundException) {
                _uiState.value = WeatherUiState.Error("位置情報を取得できませんでした。")
            } catch (e: SecurityException) {
                _uiState.value = WeatherUiState.PermissionRequired
            } catch (e: IOException) {
                _uiState.value = WeatherUiState.Error("ネットワークエラーが発生しました。インターネット接続を確認してください。")
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("エラーが発生しました: ${e.message}")
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
     * Error state with error message
     */
    data class Error(val message: String) : WeatherUiState()
    
    /**
     * Permission required state
     */
    object PermissionRequired : WeatherUiState()
}
