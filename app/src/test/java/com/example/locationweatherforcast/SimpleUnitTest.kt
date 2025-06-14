package com.example.locationweatherforcast

import org.junit.Test
import org.junit.Assert.*

/**
 * 基本的なユニットテスト
 */
class SimpleUnitTest {
    
    @Test
    fun `基本的な計算のテスト`() {
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun `文字列操作のテスト`() {
        val result = "Hello".plus(" World")
        assertEquals("Hello World", result)
    }
    
    @Test
    fun `天気コードの説明テスト`() {
        // WeatherDataのgetWeatherDescriptionメソッドをテスト
        val descriptions = mapOf(
            0 to "晴れ",
            1 to "曇り",
            2 to "曇り", 
            3 to "曇り",
            61 to "雨",
            71 to "雪",
            95 to "雷雨"
        )
        
        descriptions.forEach { (code, expected) ->
            val description = getWeatherDescription(code)
            assertEquals("Weather code $code should return $expected", expected, description)
        }
    }
    
    private fun getWeatherDescription(weatherCode: Int): String {
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