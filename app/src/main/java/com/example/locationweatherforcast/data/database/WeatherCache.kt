package com.example.locationweatherforcast.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDateTime

/**
 * Weather cache entity for storing weather data with expiration
 * Includes performance indices for common query patterns
 */
@Entity(
    tableName = "weather_cache",
    primaryKeys = ["locationKey", "date"],
    indices = [
        androidx.room.Index(
            value = ["locationKey"],
            name = "idx_weather_cache_location"
        ),
        androidx.room.Index(
            value = ["cachedAt"],
            name = "idx_weather_cache_cached_at"
        ),
        androidx.room.Index(
            value = ["expiresAt"],
            name = "idx_weather_cache_expires_at"
        ),
        androidx.room.Index(
            value = ["locationKey", "date"],
            name = "idx_weather_cache_location_date"
        )
    ]
)
data class WeatherCache(
    val locationKey: String, // Composite key: "${latitude}_${longitude}"
    val date: String, // Date in ISO format (YYYY-MM-DD)
    val weatherCode: Int,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val precipitation: Double,
    val latitude: Double,
    val longitude: Double,
    val locationName: String? = null,
    val cachedAt: String, // Cache timestamp in ISO format
    val expiresAt: String, // Expiration timestamp in ISO format
    val lastUpdated: String // Last updated timestamp in ISO format
) {
    companion object {
        /**
         * Generate location key from coordinates
         */
        fun generateLocationKey(latitude: Double, longitude: Double): String {
            return "${String.format("%.4f", latitude)}_${String.format("%.4f", longitude)}"
        }
        
        /**
         * Check if cache entry is fresh (< 15 minutes old)
         */
        fun isFresh(cachedAt: String): Boolean {
            return try {
                val cached = LocalDateTime.parse(cachedAt)
                Duration.between(cached, LocalDateTime.now()).toMinutes() < 15
            } catch (e: Exception) {
                false // Treat invalid timestamps as expired
            }
        }
        
        /**
         * Check if cache entry is stale but usable (15-60 minutes old)
         */
        fun isStale(cachedAt: String): Boolean {
            return try {
                val cached = LocalDateTime.parse(cachedAt)
                val minutesOld = Duration.between(cached, LocalDateTime.now()).toMinutes()
                minutesOld in 15..59
            } catch (e: Exception) {
                false // Treat invalid timestamps as expired
            }
        }
        
        /**
         * Check if cache entry is expired (> 60 minutes old)
         */
        fun isExpired(cachedAt: String): Boolean {
            return try {
                val cached = LocalDateTime.parse(cachedAt)
                Duration.between(cached, LocalDateTime.now()).toMinutes() >= 60
            } catch (e: Exception) {
                true // Treat invalid timestamps as expired
            }
        }
    }
}