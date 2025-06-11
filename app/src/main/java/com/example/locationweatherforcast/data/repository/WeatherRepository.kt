package com.example.locationweatherforcast.data.repository

import com.example.locationweatherforcast.data.location.LocationService
import com.example.locationweatherforcast.data.model.LocationData
import com.example.locationweatherforcast.data.model.WeatherData
import com.example.locationweatherforcast.data.remote.WeatherApiService
import com.example.locationweatherforcast.data.database.WeatherCache
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for weather-related operations
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val locationService: LocationService,
    private val weatherApiService: WeatherApiService,
    private val weatherCacheRepository: WeatherCacheRepository
) {
    /**
     * Get the weather forecast for the current location
     * @return WeatherData containing forecast information
     */
    suspend fun getWeatherForecast(): WeatherData {
        val location = locationService.getCurrentLocation()
        return getWeatherForecastForLocation(location.latitude, location.longitude)
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
     * Get the weather forecast for a specific location with caching
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param forceRefresh Force refresh from API even if cache is fresh
     * @return WeatherData containing forecast information
     */
    suspend fun getWeatherForecastForLocation(
        latitude: Double, 
        longitude: Double, 
        forceRefresh: Boolean = false
    ): WeatherData {
        val tomorrowDate = ZonedDateTime.now().plusDays(1).toLocalDate().toString()
        
        // Check cache first if not forcing refresh
        if (!forceRefresh) {
            try {
                val cachedWeather = weatherCacheRepository.getCachedWeather(latitude, longitude, tomorrowDate)
                if (cachedWeather != null && WeatherCache.isFresh(cachedWeather.cachedAt)) {
                    // Return fresh cached data
                    return convertCacheToWeatherData(cachedWeather)
                }
            } catch (e: Exception) {
                // Cache read failed, continue to API fetch
                // Log error but don't fail the entire request
            }
        }
        
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
        val weatherData = WeatherData(
            date = response.daily.time[tomorrowIndex],
            weatherCode = response.daily.weathercode[tomorrowIndex],
            temperatureMax = response.daily.temperature_2m_max[tomorrowIndex],
            temperatureMin = response.daily.temperature_2m_min[tomorrowIndex],
            precipitation = response.daily.precipitation_sum[tomorrowIndex],
            location = location,
            lastUpdated = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )
        
        // Cache the weather data (continue even if caching fails)
        try {
            weatherCacheRepository.cacheWeatherData(latitude, longitude, weatherData)
        } catch (e: Exception) {
            // Cache write failed, but return the weather data anyway
            // Log error for monitoring purposes
        }
        
        return weatherData
    }
    
    /**
     * Convert cached weather data to WeatherData model
     */
    private fun convertCacheToWeatherData(cache: WeatherCache): WeatherData {
        return WeatherData(
            date = cache.date,
            weatherCode = cache.weatherCode,
            temperatureMax = cache.temperatureMax,
            temperatureMin = cache.temperatureMin,
            precipitation = cache.precipitation,
            location = LocationData(
                latitude = cache.latitude,
                longitude = cache.longitude
            ),
            lastUpdated = cache.lastUpdated
        )
    }
    
    /**
     * Clean up expired cache entries
     */
    suspend fun cleanupCache() {
        weatherCacheRepository.cleanupExpiredCache()
    }
    
    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats {
        return weatherCacheRepository.getCacheStats()
    }
}
