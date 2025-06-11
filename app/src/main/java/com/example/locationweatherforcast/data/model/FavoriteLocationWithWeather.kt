package com.example.locationweatherforcast.data.model

/**
 * Data class combining favorite location with its weather data
 */
data class FavoriteLocationWithWeather(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val order: Int,
    val weatherData: WeatherData?
)