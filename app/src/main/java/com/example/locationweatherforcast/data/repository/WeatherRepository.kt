package com.example.locationweatherforcast.data.repository

import com.example.locationweatherforcast.data.location.LocationService
import com.example.locationweatherforcast.data.model.LocationData
import com.example.locationweatherforcast.data.model.WeatherData
import com.example.locationweatherforcast.data.remote.NetworkModule
import com.example.locationweatherforcast.data.remote.WeatherApiService
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Repository for weather-related operations
 */
class WeatherRepository(
    private val locationService: LocationService,
    private val weatherApiService: WeatherApiService = NetworkModule.weatherApiService
) {
    /**
     * Get the weather forecast for the current location
     * @return WeatherData containing forecast information
     */
    suspend fun getWeatherForecast(): WeatherData {
        // Get current location
        val location = locationService.getCurrentLocation()
        
        // Fetch weather data from API
        val response = weatherApiService.getWeatherForecast(
            latitude = location.latitude,
            longitude = location.longitude
        )
        
        // Extract tomorrow's forecast (index 0 is today, 1 is tomorrow)
        val tomorrowIndex = 1
        
        // Map API response to our data model
        return WeatherData(
            date = response.daily.time[tomorrowIndex],
            weatherCode = response.daily.weathercode[tomorrowIndex],
            temperatureMax = response.daily.temperature_2m_max[tomorrowIndex],
            temperatureMin = response.daily.temperature_2m_min[tomorrowIndex],
            precipitation = response.daily.precipitation_sum[tomorrowIndex],
            location = location,
            lastUpdated = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
    }
    
    /**
     * Check if location permissions are granted
     * @return true if permissions are granted, false otherwise
     */
    fun hasLocationPermission(): Boolean {
        return locationService.hasLocationPermission()
    }
    
    /**
     * Check if location services are enabled
     * @return true if location services are enabled, false otherwise
     */
    fun isLocationEnabled(): Boolean {
        return locationService.isLocationEnabled()
    }
    
    /**
     * Get the weather forecast for a specific location
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return WeatherData containing forecast information
     */
    suspend fun getWeatherForecastForLocation(latitude: Double, longitude: Double): WeatherData {
        // Fetch weather data from API
        val response = weatherApiService.getWeatherForecast(
            latitude = latitude,
            longitude = longitude
        )
        
        // Extract tomorrow's forecast (index 0 is today, 1 is tomorrow)
        val tomorrowIndex = 1
        
        // Create location data
        val location = LocationData(
            latitude = latitude,
            longitude = longitude
        )
        
        // Map API response to our data model
        return WeatherData(
            date = response.daily.time[tomorrowIndex],
            weatherCode = response.daily.weathercode[tomorrowIndex],
            temperatureMax = response.daily.temperature_2m_max[tomorrowIndex],
            temperatureMin = response.daily.temperature_2m_min[tomorrowIndex],
            precipitation = response.daily.precipitation_sum[tomorrowIndex],
            location = location,
            lastUpdated = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
    }
}
