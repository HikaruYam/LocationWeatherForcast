package com.example.locationweatherforcast.data.repository

import com.example.locationweatherforcast.data.database.WeatherCache
import com.example.locationweatherforcast.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for weather cache operations
 */
interface WeatherCacheRepository {
    
    /**
     * Get cached weather data for a location and date
     */
    suspend fun getCachedWeather(latitude: Double, longitude: Double, date: String): WeatherCache?
    
    /**
     * Cache weather data for a location
     */
    suspend fun cacheWeatherData(
        latitude: Double,
        longitude: Double,
        weatherData: WeatherData,
        locationName: String? = null
    )
    
    /**
     * Get all cached weather for a location
     */
    suspend fun getAllCachedWeatherForLocation(latitude: Double, longitude: Double): List<WeatherCache>
    
    /**
     * Get all cached weather as Flow for reactive updates
     */
    fun getAllCachedWeatherFlow(): Flow<List<WeatherCache>>
    
    /**
     * Delete all cache for a location
     */
    suspend fun deleteAllCacheForLocation(latitude: Double, longitude: Double)
    
    /**
     * Clean up expired cache entries
     */
    suspend fun cleanupExpiredCache()
    
    /**
     * Get stale cache entries that need background refresh
     */
    suspend fun getStaleCache(): List<WeatherCache>
    
    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats
    
    /**
     * Clear all cache entries
     */
    suspend fun clearAllCache()
}

/**
 * Cache statistics data class
 */
data class CacheStats(
    val totalEntries: Int,
    val sizeBytes: Long,
    val freshEntries: Int,
    val staleEntries: Int,
    val expiredEntries: Int
)