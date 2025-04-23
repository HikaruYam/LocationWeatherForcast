package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Weather icon component based on weather code
 */
@Composable
fun WeatherIcon(
    weatherCode: Int,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    when (weatherCode) {
        // Clear sky
        0 -> SunnyIcon(modifier = modifier, tint = tint)
        
        // Mainly clear, partly cloudy, and overcast
        1, 2, 3 -> CloudyIcon(modifier = modifier, tint = tint)
        
        // Fog and depositing rime fog
        45, 48 -> FogIcon(modifier = modifier, tint = tint)
        
        // Drizzle: Light, moderate, and dense intensity
        51, 53, 55 -> DrizzleIcon(modifier = modifier, tint = tint)
        
        // Freezing Drizzle: Light and dense intensity
        56, 57 -> FreezingRainIcon(modifier = modifier, tint = tint)
        
        // Rain: Slight, moderate and heavy intensity
        61, 63, 65 -> RainIcon(modifier = modifier, tint = tint)
        
        // Freezing Rain: Light and heavy intensity
        66, 67 -> FreezingRainIcon(modifier = modifier, tint = tint)
        
        // Snow fall: Slight, moderate, and heavy intensity
        71, 73, 75 -> SnowIcon(modifier = modifier, tint = tint)
        
        // Snow grains
        77 -> SnowIcon(modifier = modifier, tint = tint)
        
        // Rain showers: Slight, moderate, and violent
        80, 81, 82 -> RainIcon(modifier = modifier, tint = tint)
        
        // Snow showers slight and heavy
        85, 86 -> SnowIcon(modifier = modifier, tint = tint)
        
        // Thunderstorm: Slight or moderate
        95 -> ThunderstormIcon(modifier = modifier, tint = tint)
        
        // Thunderstorm with slight and heavy hail
        96, 99 -> ThunderstormWithHailIcon(modifier = modifier, tint = tint)
        
        // Default
        else -> UnknownIcon(modifier = modifier, tint = tint)
    }
}

/**
 * Sunny icon
 */
@Composable
fun SunnyIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFFB300)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 4
        
        // Draw sun circle
        drawCircle(
            color = tint,
            radius = radius,
            center = center
        )
        
        // Draw sun rays
        val rayLength = radius * 0.7f
        val startRadius = radius * 1.2f
        val endRadius = startRadius + rayLength
        
        for (i in 0 until 8) {
            val angle = Math.toRadians(i * 45.0).toFloat()
            val startX = center.x + startRadius * kotlin.math.cos(angle)
            val startY = center.y + startRadius * kotlin.math.sin(angle)
            val endX = center.x + endRadius * kotlin.math.cos(angle)
            val endY = center.y + endRadius * kotlin.math.sin(angle)
            
            drawLine(
                color = tint,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 4f
            )
        }
    }
}

/**
 * Cloudy icon
 */
@Composable
fun CloudyIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF78909C)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = tint
        val sunColor = Color(0xFFFFB300)
        
        // Draw sun (smaller and offset)
        val sunCenter = Offset(size.width * 0.35f, size.height * 0.4f)
        val sunRadius = size.minDimension / 6
        
        drawCircle(
            color = sunColor,
            radius = sunRadius,
            center = sunCenter
        )
        
        // Draw cloud
        val cloudCenterX = size.width * 0.6f
        val cloudCenterY = size.height * 0.6f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.6f,
            center = Offset(cloudCenterX, cloudCenterY - cloudRadius * 0.5f)
        )
    }
}

/**
 * Fog icon
 */
@Composable
fun FogIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFB0BEC5)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val fogColor = tint
        val lineSpacing = size.height / 7
        val lineLength = size.width * 0.8f
        val startX = size.width * 0.1f
        
        // Draw fog lines
        for (i in 1..5) {
            val y = i * lineSpacing
            
            drawLine(
                color = fogColor,
                start = Offset(startX, y),
                end = Offset(startX + lineLength, y),
                strokeWidth = 4f
            )
        }
    }
}

/**
 * Drizzle icon
 */
@Composable
fun DrizzleIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64B5F6)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = Color(0xFF78909C)
        val rainColor = tint
        
        // Draw cloud
        val cloudCenterX = size.width * 0.5f
        val cloudCenterY = size.height * 0.4f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        // Draw rain drops (fewer for drizzle)
        val dropStartY = cloudCenterY + cloudRadius * 0.8f
        val dropLength = size.height * 0.1f
        
        for (i in 0 until 3) {
            val x = size.width * (0.3f + i * 0.2f)
            
            drawLine(
                color = rainColor,
                start = Offset(x, dropStartY),
                end = Offset(x, dropStartY + dropLength),
                strokeWidth = 2f
            )
        }
    }
}

/**
 * Rain icon
 */
@Composable
fun RainIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF2196F3)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = Color(0xFF546E7A)
        val rainColor = tint
        
        // Draw cloud
        val cloudCenterX = size.width * 0.5f
        val cloudCenterY = size.height * 0.4f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        // Draw rain drops
        val dropStartY = cloudCenterY + cloudRadius * 0.8f
        val dropLength = size.height * 0.15f
        
        for (i in 0 until 5) {
            val x = size.width * (0.2f + i * 0.15f)
            
            drawLine(
                color = rainColor,
                start = Offset(x, dropStartY),
                end = Offset(x, dropStartY + dropLength),
                strokeWidth = 3f
            )
        }
    }
}

/**
 * Freezing rain icon
 */
@Composable
fun FreezingRainIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF90CAF9)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = Color(0xFF546E7A)
        val rainColor = tint
        val iceColor = Color(0xFFB3E5FC)
        
        // Draw cloud
        val cloudCenterX = size.width * 0.5f
        val cloudCenterY = size.height * 0.4f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        // Draw rain/ice drops
        val dropStartY = cloudCenterY + cloudRadius * 0.8f
        val dropLength = size.height * 0.12f
        
        for (i in 0 until 4) {
            val x = size.width * (0.25f + i * 0.17f)
            
            // Rain drop
            drawLine(
                color = rainColor,
                start = Offset(x, dropStartY),
                end = Offset(x, dropStartY + dropLength),
                strokeWidth = 2.5f
            )
            
            // Ice crystal at the bottom
            drawCircle(
                color = iceColor,
                radius = 3f,
                center = Offset(x, dropStartY + dropLength + 3f)
            )
        }
    }
}

/**
 * Snow icon
 */
@Composable
fun SnowIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFE1F5FE)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = Color(0xFF78909C)
        val snowColor = tint
        
        // Draw cloud
        val cloudCenterX = size.width * 0.5f
        val cloudCenterY = size.height * 0.4f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        // Draw snowflakes
        val snowflakeY = arrayOf(
            cloudCenterY + cloudRadius * 1.2f,
            cloudCenterY + cloudRadius * 1.6f,
            cloudCenterY + cloudRadius * 2.0f
        )
        
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val x = size.width * (0.3f + col * 0.2f)
                val y = snowflakeY[row]
                
                // Draw a simple snowflake
                val flakeSize = 4f
                
                // Horizontal line
                drawLine(
                    color = snowColor,
                    start = Offset(x - flakeSize, y),
                    end = Offset(x + flakeSize, y),
                    strokeWidth = 2f
                )
                
                // Vertical line
                drawLine(
                    color = snowColor,
                    start = Offset(x, y - flakeSize),
                    end = Offset(x, y + flakeSize),
                    strokeWidth = 2f
                )
                
                // Diagonal lines
                drawLine(
                    color = snowColor,
                    start = Offset(x - flakeSize * 0.7f, y - flakeSize * 0.7f),
                    end = Offset(x + flakeSize * 0.7f, y + flakeSize * 0.7f),
                    strokeWidth = 2f
                )
                
                drawLine(
                    color = snowColor,
                    start = Offset(x - flakeSize * 0.7f, y + flakeSize * 0.7f),
                    end = Offset(x + flakeSize * 0.7f, y - flakeSize * 0.7f),
                    strokeWidth = 2f
                )
            }
        }
    }
}

/**
 * Thunderstorm icon
 */
@Composable
fun ThunderstormIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFFEB3B)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = Color(0xFF455A64)
        val lightningColor = tint
        
        // Draw cloud
        val cloudCenterX = size.width * 0.5f
        val cloudCenterY = size.height * 0.4f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        // Draw lightning bolt
        val boltPoints = listOf(
            Offset(size.width * 0.5f, cloudCenterY + cloudRadius * 0.5f),
            Offset(size.width * 0.4f, cloudCenterY + cloudRadius * 1.2f),
            Offset(size.width * 0.5f, cloudCenterY + cloudRadius * 1.2f),
            Offset(size.width * 0.4f, cloudCenterY + cloudRadius * 2.0f)
        )
        
        for (i in 0 until boltPoints.size - 1) {
            drawLine(
                color = lightningColor,
                start = boltPoints[i],
                end = boltPoints[i + 1],
                strokeWidth = 3f
            )
        }
    }
}

/**
 * Thunderstorm with hail icon
 */
@Composable
fun ThunderstormWithHailIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFFEB3B)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val cloudColor = Color(0xFF37474F)
        val lightningColor = tint
        val hailColor = Color(0xFFE1F5FE)
        
        // Draw cloud
        val cloudCenterX = size.width * 0.5f
        val cloudCenterY = size.height * 0.4f
        val cloudRadius = size.minDimension / 5
        
        // Main cloud circle
        drawCircle(
            color = cloudColor,
            radius = cloudRadius,
            center = Offset(cloudCenterX, cloudCenterY)
        )
        
        // Additional cloud circles
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.8f,
            center = Offset(cloudCenterX - cloudRadius * 0.7f, cloudCenterY)
        )
        
        drawCircle(
            color = cloudColor,
            radius = cloudRadius * 0.7f,
            center = Offset(cloudCenterX + cloudRadius * 0.7f, cloudCenterY)
        )
        
        // Draw lightning bolt
        val boltPoints = listOf(
            Offset(size.width * 0.4f, cloudCenterY + cloudRadius * 0.5f),
            Offset(size.width * 0.3f, cloudCenterY + cloudRadius * 1.2f),
            Offset(size.width * 0.4f, cloudCenterY + cloudRadius * 1.2f),
            Offset(size.width * 0.3f, cloudCenterY + cloudRadius * 1.8f)
        )
        
        for (i in 0 until boltPoints.size - 1) {
            drawLine(
                color = lightningColor,
                start = boltPoints[i],
                end = boltPoints[i + 1],
                strokeWidth = 3f
            )
        }
        
        // Draw hail
        val hailY = arrayOf(
            cloudCenterY + cloudRadius * 1.0f,
            cloudCenterY + cloudRadius * 1.5f,
            cloudCenterY + cloudRadius * 2.0f
        )
        
        for (row in 0 until 3) {
            val x = size.width * (0.6f + row * 0.1f)
            val y = hailY[row]
            
            // Draw hail as small circles
            drawCircle(
                color = hailColor,
                radius = 3f,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Unknown weather icon
 */
@Composable
fun UnknownIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF9E9E9E)
) {
    Canvas(modifier = modifier.size(48.dp)) {
        val questionMarkColor = tint
        
        // Draw circle
        drawCircle(
            color = questionMarkColor,
            style = Stroke(width = 3f),
            radius = size.minDimension / 3,
            center = Offset(size.width / 2, size.height / 2)
        )
        
        // Draw question mark (simplified)
        val centerX = size.width / 2
        val centerY = size.height / 2
        val questionMarkSize = size.minDimension / 6
        
        // Question mark curve
        drawArc(
            color = questionMarkColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - questionMarkSize, centerY - questionMarkSize * 1.5f),
            size = androidx.compose.ui.geometry.Size(questionMarkSize * 2, questionMarkSize * 2),
            style = Stroke(width = 3f)
        )
        
        // Question mark dot
        drawCircle(
            color = questionMarkColor,
            radius = 3f,
            center = Offset(centerX, centerY + questionMarkSize)
        )
    }
}
