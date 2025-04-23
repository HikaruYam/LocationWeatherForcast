package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.locationweatherforcast.data.model.WeatherData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Weather card component to display weather information
 */
@Composable
fun WeatherCard(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date
            val date = LocalDate.parse(weatherData.date)
            val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weather icon and description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIcon(
                    weatherCode = weatherData.weatherCode,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = weatherData.getWeatherDescription(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Location
            Text(
                text = "位置情報: ${weatherData.location.latitude.toFixed(4)}°, ${weatherData.location.longitude.toFixed(4)}°",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Temperature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TemperatureItem(
                    label = "最高気温",
                    temperature = weatherData.temperatureMax,
                    color = Color(0xFFFF5722)
                )
                
                TemperatureItem(
                    label = "最低気温",
                    temperature = weatherData.temperatureMin,
                    color = Color(0xFF2196F3)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Precipitation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "降水量",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "${weatherData.precipitation} mm",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF03A9F4)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Last updated
            Text(
                text = "最終更新: ${weatherData.lastUpdated}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Temperature item component
 */
@Composable
fun TemperatureItem(
    label: String,
    temperature: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Text(
            text = "${temperature.toInt()}°C",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Loading component
 */
@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "天気予報を取得中...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Error component
 */
@Composable
fun ErrorComponent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "エラー",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("再試行")
            }
        }
    }
}

/**
 * Permission request component
 */
@Composable
fun PermissionRequestComponent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "位置情報の許可が必要です",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "天気予報を表示するには、位置情報へのアクセスを許可してください。",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("許可する")
            }
        }
    }
}

/**
 * Extension function to format Double to fixed decimal places
 */
private fun Double.toFixed(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
