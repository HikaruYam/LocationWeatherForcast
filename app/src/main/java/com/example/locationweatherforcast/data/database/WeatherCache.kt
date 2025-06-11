package com.example.locationweatherforcast.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Weather cache entity for storing weather data with expiration
 */
@Entity(
    tableName = "weather_cache",
    primaryKeys = ["locationKey", "date"]
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
            val cached = LocalDateTime.parse(cachedAt)
            val now = LocalDateTime.now()
            return cached.plusMinutes(15).isAfter(now)
        }
        
        /**
         * Check if cache entry is stale but usable (15-60 minutes old)
         */
        fun isStale(cachedAt: String): Boolean {
            val cached = LocalDateTime.parse(cachedAt)
            val now = LocalDateTime.now()
            return cached.plusMinutes(15).isBefore(now) && cached.plusMinutes(60).isAfter(now)
        }
        
        /**
         * Check if cache entry is expired (> 60 minutes old)
         */
        fun isExpired(cachedAt: String): Boolean {
            val cached = LocalDateTime.parse(cachedAt)
            val now = LocalDateTime.now()
            return cached.plusMinutes(60).isBefore(now)
        }
    }
}