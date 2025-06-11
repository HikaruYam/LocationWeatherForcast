package com.example.locationweatherforcast.data.repository

import com.example.locationweatherforcast.data.database.WeatherCache
import com.example.locationweatherforcast.data.database.WeatherCacheDao
import com.example.locationweatherforcast.data.model.WeatherData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WeatherCacheRepository
 */
@Singleton
class WeatherCacheRepositoryImpl @Inject constructor(
    private val weatherCacheDao: WeatherCacheDao
) : WeatherCacheRepository {
    
    override suspend fun getCachedWeather(
        latitude: Double,
        longitude: Double,
        date: String
    ): WeatherCache? {
        val locationKey = WeatherCache.generateLocationKey(latitude, longitude)
        return weatherCacheDao.getCachedWeather(locationKey, date)
    }
    
    override suspend fun cacheWeatherData(
        latitude: Double,
        longitude: Double,
        weatherData: WeatherData,
        locationName: String?
    ) {
        val locationKey = WeatherCache.generateLocationKey(latitude, longitude)
        val now = LocalDateTime.now()
        val cachedAt = now.toString()
        val expiresAt = now.plusMinutes(60).toString()
        
        val weatherCache = WeatherCache(
            locationKey = locationKey,
            date = weatherData.date,
            weatherCode = weatherData.weatherCode,
            temperatureMax = weatherData.temperatureMax,
            temperatureMin = weatherData.temperatureMin,
            precipitation = weatherData.precipitation,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            cachedAt = cachedAt,
            expiresAt = expiresAt,
            lastUpdated = cachedAt
        )
        
        weatherCacheDao.insertWeatherCache(weatherCache)
    }
    
    override suspend fun getAllCachedWeatherForLocation(
        latitude: Double,
        longitude: Double
    ): List<WeatherCache> {
        val locationKey = WeatherCache.generateLocationKey(latitude, longitude)
        return weatherCacheDao.getAllCachedWeatherForLocation(locationKey)
    }
    
    override fun getAllCachedWeatherFlow(): Flow<List<WeatherCache>> {
        return weatherCacheDao.getAllCachedWeatherFlow()
    }
    
    override suspend fun deleteAllCacheForLocation(latitude: Double, longitude: Double) {
        val locationKey = WeatherCache.generateLocationKey(latitude, longitude)
        weatherCacheDao.deleteAllCacheForLocation(locationKey)
    }
    
    override suspend fun cleanupExpiredCache() {
        val currentTime = LocalDateTime.now().toString()
        weatherCacheDao.deleteExpiredCache(currentTime)
    }
    
    override suspend fun getStaleCache(): List<WeatherCache> {
        val now = LocalDateTime.now()
        val staleThreshold = now.minusMinutes(15).toString()
        val expiredThreshold = now.minusMinutes(60).toString()
        return weatherCacheDao.getStaleCache(staleThreshold, expiredThreshold)
    }
    
    override suspend fun getCacheStats(): CacheStats {
        val totalEntries = weatherCacheDao.getCacheCount()
        val sizeBytes = weatherCacheDao.getCacheSizeBytes()
        
        // Calculate fresh, stale, and expired entries
        val allCache = weatherCacheDao.getAllCachedWeatherFlow()
        var freshEntries = 0
        var staleEntries = 0
        var expiredEntries = 0
        
        // Get current cache snapshot for stats calculation
        val allCacheEntries = weatherCacheDao.getOldestCacheEntries(totalEntries)
        
        allCacheEntries.forEach { cache ->
            when {
                WeatherCache.isFresh(cache.cachedAt) -> freshEntries++
                WeatherCache.isStale(cache.cachedAt) -> staleEntries++
                WeatherCache.isExpired(cache.cachedAt) -> expiredEntries++
            }
        }
        
        return CacheStats(
            totalEntries = totalEntries,
            sizeBytes = sizeBytes,
            freshEntries = freshEntries,
            staleEntries = staleEntries,
            expiredEntries = expiredEntries
        )
    }
    
    override suspend fun clearAllCache() {
        weatherCacheDao.clearAllCache()
    }
}