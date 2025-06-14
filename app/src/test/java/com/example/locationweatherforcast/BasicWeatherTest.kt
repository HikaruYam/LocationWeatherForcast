package com.example.locationweatherforcast

import com.example.locationweatherforcast.data.model.WeatherData
import com.example.locationweatherforcast.data.model.LocationData
import org.junit.Test
import org.junit.Assert.*

/**
 * WeatherDataの基本的なテスト
 */
class BasicWeatherTest {
    
    @Test
    fun `WeatherDataの作成テスト`() {
        val location = LocationData(35.6762, 139.6503, "Tokyo")
        val weatherData = WeatherData(
            date = "2024-01-01",
            weatherCode = 0,
            temperatureMax = 25.0,
            temperatureMin = 15.0,
            precipitation = 0.0,
            location = location,
            lastUpdated = "2024-01-01 12:00:00"
        )
        
        assertEquals("2024-01-01", weatherData.date)
        assertEquals(0, weatherData.weatherCode)
        assertEquals(25.0, weatherData.temperatureMax, 0.1)
        assertEquals(15.0, weatherData.temperatureMin, 0.1)
        assertEquals(0.0, weatherData.precipitation, 0.1)
        assertEquals("Tokyo", weatherData.location.name)
    }
    
    @Test
    fun `天気コードの説明テスト`() {
        val location = LocationData(35.6762, 139.6503, "Tokyo")
        
        // 晴れ
        val clearWeather = WeatherData(
            date = "2024-01-01",
            weatherCode = 0,
            temperatureMax = 25.0,
            temperatureMin = 15.0,
            precipitation = 0.0,
            location = location,
            lastUpdated = "2024-01-01 12:00:00"
        )
        assertEquals("晴れ", clearWeather.getWeatherDescription())
        
        // 曇り
        val cloudyWeather = WeatherData(
            date = "2024-01-01",
            weatherCode = 1,
            temperatureMax = 20.0,
            temperatureMin = 10.0,
            precipitation = 0.0,
            location = location,
            lastUpdated = "2024-01-01 12:00:00"
        )
        assertEquals("曇り", cloudyWeather.getWeatherDescription())
        
        // 雨
        val rainyWeather = WeatherData(
            date = "2024-01-01",
            weatherCode = 61,
            temperatureMax = 18.0,
            temperatureMin = 12.0,
            precipitation = 5.0,
            location = location,
            lastUpdated = "2024-01-01 12:00:00"
        )
        assertEquals("雨", rainyWeather.getWeatherDescription())
        
        // 雪
        val snowyWeather = WeatherData(
            date = "2024-01-01",
            weatherCode = 71,
            temperatureMax = 2.0,
            temperatureMin = -3.0,
            precipitation = 3.0,
            location = location,
            lastUpdated = "2024-01-01 12:00:00"
        )
        assertEquals("雪", snowyWeather.getWeatherDescription())
        
        // 不明
        val unknownWeather = WeatherData(
            date = "2024-01-01",
            weatherCode = 999,
            temperatureMax = 20.0,
            temperatureMin = 10.0,
            precipitation = 0.0,
            location = location,
            lastUpdated = "2024-01-01 12:00:00"
        )
        assertEquals("不明", unknownWeather.getWeatherDescription())
    }
    
    @Test
    fun `LocationDataの作成テスト`() {
        val location = LocationData(35.6762, 139.6503, "Tokyo")
        
        assertEquals(35.6762, location.latitude, 0.0001)
        assertEquals(139.6503, location.longitude, 0.0001)
        assertEquals("Tokyo", location.name)
    }
    
    @Test
    fun `LocationDataの名前なし作成テスト`() {
        val location = LocationData(35.6762, 139.6503)
        
        assertEquals(35.6762, location.latitude, 0.0001)
        assertEquals(139.6503, location.longitude, 0.0001)
        assertNull(location.name)
    }
}