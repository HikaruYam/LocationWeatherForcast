package com.example.locationweatherforcast.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Represents weather forecast data for a specific day
 */
@Serializable
data class WeatherData(
    val date: String, // Date in ISO format (YYYY-MM-DD)
    val weatherCode: Int, // Weather code according to WMO standards
    val temperatureMax: Double, // Maximum temperature in Celsius
    val temperatureMin: Double, // Minimum temperature in Celsius
    val precipitation: Double, // Precipitation sum in mm
    val location: LocationData, // Location information
    val lastUpdated: String // Last updated timestamp in ISO format
) {
    /**
     * Returns a human-readable description of the weather based on the weather code
     */
    fun getWeatherDescription(): String {
        return when (weatherCode) {
            0 -> "晴れ" // Clear sky
            1, 2, 3 -> "曇り" // Mainly clear, partly cloudy, and overcast
            45, 48 -> "霧" // Fog and depositing rime fog
            51, 53, 55 -> "小雨" // Drizzle: Light, moderate, and dense intensity
            56, 57 -> "凍雨" // Freezing Drizzle: Light and dense intensity
            61, 63, 65 -> "雨" // Rain: Slight, moderate and heavy intensity
            66, 67 -> "凍雨" // Freezing Rain: Light and heavy intensity
            71, 73, 75 -> "雪" // Snow fall: Slight, moderate, and heavy intensity
            77 -> "雪粒" // Snow grains
            80, 81, 82 -> "にわか雨" // Rain showers: Slight, moderate, and violent
            85, 86 -> "にわか雪" // Snow showers slight and heavy
            95 -> "雷雨" // Thunderstorm: Slight or moderate
            96, 99 -> "雷雨と雹" // Thunderstorm with slight and heavy hail
            else -> "不明" // Unknown
        }
    }
}

/**
 * Represents location data
 */
@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null // Optional location name
)

/**
 * API response model for Open-Meteo API
 */
@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val daily: DailyWeather,
    val daily_units: DailyUnits
)

/**
 * Daily weather data from Open-Meteo API
 */
@Serializable
data class DailyWeather(
    val time: List<String>,
    val weathercode: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val precipitation_sum: List<Double>
)

/**
 * Units for daily weather data
 */
@Serializable
data class DailyUnits(
    val time: String,
    val weathercode: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val precipitation_sum: String
)
