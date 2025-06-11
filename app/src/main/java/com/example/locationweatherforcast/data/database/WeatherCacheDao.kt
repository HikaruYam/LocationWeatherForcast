package com.example.locationweatherforcast.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for weather cache operations
 */
@Dao
interface WeatherCacheDao {
    
    /**
     * Get cached weather data for a specific location and date
     */
    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey AND date = :date")
    suspend fun getCachedWeather(locationKey: String, date: String): WeatherCache?
    
    /**
     * Get all cached weather data for a location
     */
    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey ORDER BY date ASC")
    suspend fun getAllCachedWeatherForLocation(locationKey: String): List<WeatherCache>
    
    /**
     * Get all cached weather data as Flow for reactive updates
     */
    @Query("SELECT * FROM weather_cache ORDER BY cachedAt DESC")
    fun getAllCachedWeatherFlow(): Flow<List<WeatherCache>>
    
    /**
     * Insert or replace weather cache entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(weatherCache: WeatherCache)
    
    /**
     * Insert multiple weather cache entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCacheList(weatherCacheList: List<WeatherCache>)
    
    /**
     * Update weather cache entry
     */
    @Update
    suspend fun updateWeatherCache(weatherCache: WeatherCache)
    
    /**
     * Delete weather cache entry
     */
    @Query("DELETE FROM weather_cache WHERE locationKey = :locationKey AND date = :date")
    suspend fun deleteWeatherCache(locationKey: String, date: String)
    
    /**
     * Delete all cache entries for a location
     */
    @Query("DELETE FROM weather_cache WHERE locationKey = :locationKey")
    suspend fun deleteAllCacheForLocation(locationKey: String)
    
    /**
     * Delete expired cache entries (older than specified timestamp)
     */
    @Query("DELETE FROM weather_cache WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredCache(currentTime: String)
    
    /**
     * Get cache entries that need refresh (stale but not expired)
     */
    @Query("SELECT * FROM weather_cache WHERE cachedAt < :staleThreshold AND cachedAt > :expiredThreshold")
    suspend fun getStaleCache(staleThreshold: String, expiredThreshold: String): List<WeatherCache>
    
    /**
     * Get total count of cache entries
     */
    @Query("SELECT COUNT(*) FROM weather_cache")
    suspend fun getCacheCount(): Int
    
    /**
     * Get cache size in terms of storage (approximate)
     */
    @Query("SELECT COUNT(*) * 200 FROM weather_cache") // Approximate 200 bytes per entry
    suspend fun getCacheSizeBytes(): Long
    
    /**
     * Clear all cache entries
     */
    @Query("DELETE FROM weather_cache")
    suspend fun clearAllCache()
    
    /**
     * Get oldest cache entries for cleanup
     */
    @Query("SELECT * FROM weather_cache ORDER BY cachedAt ASC LIMIT :limit")
    suspend fun getOldestCacheEntries(limit: Int): List<WeatherCache>
}