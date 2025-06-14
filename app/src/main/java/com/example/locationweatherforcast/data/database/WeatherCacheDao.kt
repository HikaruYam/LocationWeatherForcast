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
     * Performance: Uses composite index on locationKey and date
     */
    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey AND date = :date LIMIT 1")
    suspend fun getCachedWeather(locationKey: String, date: String): WeatherCache?
    
    /**
     * Get all cached weather data for a location
     * Performance: Uses index on locationKey
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
     * Performance: Uses index on expiresAt for efficient cleanup
     */
    @Query("DELETE FROM weather_cache WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredCache(currentTime: String)
    
    /**
     * Batch delete cache entries by location keys for better performance
     */
    @Query("DELETE FROM weather_cache WHERE locationKey IN (:locationKeys)")
    suspend fun deleteCacheByLocationKeys(locationKeys: List<String>)
    
    /**
     * Get fresh cache entries (for performance monitoring)
     */
    @Query("SELECT * FROM weather_cache WHERE cachedAt > :freshThreshold ORDER BY cachedAt DESC")
    suspend fun getFreshCache(freshThreshold: String): List<WeatherCache>
    
    /**
     * Batch update cache timestamps for performance
     */
    @Query("UPDATE weather_cache SET lastUpdated = :timestamp WHERE locationKey IN (:locationKeys)")
    suspend fun batchUpdateLastUpdated(locationKeys: List<String>, timestamp: String)
    
    /**
     * Get cache entries that need refresh (stale but not expired)
     * Performance: Uses index on cachedAt for efficient timestamp filtering
     */
    @Query("SELECT * FROM weather_cache WHERE cachedAt < :staleThreshold AND cachedAt > :expiredThreshold ORDER BY cachedAt ASC")
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