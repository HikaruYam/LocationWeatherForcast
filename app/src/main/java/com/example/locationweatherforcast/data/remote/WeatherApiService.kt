package com.example.locationweatherforcast.data.remote

import com.example.locationweatherforcast.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for Open-Meteo API
 */
interface WeatherApiService {
    /**
     * Get weather forecast data
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param daily Weather parameters to include in the response
     * @param timezone Timezone for the data
     * @param forecastDays Number of days to forecast
     * @return WeatherResponse object containing forecast data
     */
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,precipitation_sum",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 1
    ): WeatherResponse
}
