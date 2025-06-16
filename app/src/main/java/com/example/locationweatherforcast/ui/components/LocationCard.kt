package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.locationweatherforcast.data.model.FavoriteLocationWithWeather

@Composable
fun LocationCard(
    location: FavoriteLocationWithWeather,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    isDragEnabled: Boolean = false,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Create comprehensive accessibility description
    val cardDescription = buildString {
        append("場所: ${location.name}")
        if (location.weatherData != null) {
            append(", 天気: ${location.weatherData.getWeatherDescription()}")
            append(", 最高気温: ${location.weatherData.temperatureMax.toInt()}度")
            append(", 最低気温: ${location.weatherData.temperatureMin.toInt()}度")
        } else {
            append(", 天気データなし")
        }
        if (isLoading) {
            append(", 天気データ読み込み中")
        }
        append(", タップして詳細を表示")
    }

    val cardStateDescription = when {
        isLoading -> "読み込み中"
        location.weatherData != null -> "天気データ利用可能"
        else -> "天気データなし"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = "${location.name}の詳細を表示"
            ) { onCardClick() }
            .semantics {
                contentDescription = cardDescription
                stateDescription = cardStateDescription
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle (if drag is enabled)
            if (isDragEnabled) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "${location.name}を長押しして並び替え",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .semantics {
                            contentDescription = "${location.name}の位置を変更するには長押ししてドラッグしてください"
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Location info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = location.weatherData?.getWeatherDescription() ?: "データなし",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${String.format("%.2f", location.latitude)}, ${String.format("%.2f", location.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Weather info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    PulsingLoadingIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .semantics {
                                contentDescription = "${location.name}の天気データを読み込んでいます"
                            }
                    )
                } else {
                    WeatherIcon(
                        weatherCode = location.weatherData?.weatherCode ?: 0,
                        modifier = Modifier
                            .size(32.dp)
                            .semantics {
                                contentDescription = location.weatherData?.getWeatherDescription() ?: "天気情報なし"
                            }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        contentDescription = if (location.weatherData != null) {
                            "最高気温 ${location.weatherData.temperatureMax.toInt()}度, 最低気温 ${location.weatherData.temperatureMin.toInt()}度"
                        } else {
                            "気温データなし"
                        }
                    }
                ) {
                    Text(
                        text = "${location.weatherData?.temperatureMax?.toInt() ?: "--"}°C",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clearAndSetSemantics { }
                    )
                    if (location.weatherData != null) {
                        Text(
                            text = "${location.weatherData.temperatureMin.toInt()}°C",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clearAndSetSemantics { }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Action buttons
            Row {
                // Refresh button
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier
                        .size(40.dp)
                        .semantics {
                            contentDescription = "${location.name}の天気データを更新"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null, // Parent handles description
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(40.dp)
                        .semantics {
                            contentDescription = "${location.name}を削除"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null, // Parent handles description
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleLocationCard(
    location: FavoriteLocationWithWeather,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cardDescription = buildString {
        append("場所: ${location.name}")
        if (location.weatherData != null) {
            append(", 天気: ${location.weatherData.getWeatherDescription()}")
            append(", 最高気温: ${location.weatherData.temperatureMax.toInt()}度")
        } else {
            append(", 天気データなし")
        }
        append(", タップして選択")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClickLabel = "${location.name}を選択"
            ) { onCardClick() }
            .semantics {
                contentDescription = cardDescription
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = location.weatherData?.getWeatherDescription() ?: "--",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = if (location.weatherData != null) {
                        "${location.weatherData.getWeatherDescription()}, ${location.weatherData.temperatureMax.toInt()}度"
                    } else {
                        "天気データなし"
                    }
                }
            ) {
                WeatherIcon(
                    weatherCode = location.weatherData?.weatherCode ?: 0,
                    modifier = Modifier
                        .size(20.dp)
                        .clearAndSetSemantics { }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${location.weatherData?.temperatureMax?.toInt() ?: "--"}°",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clearAndSetSemantics { }
                )
            }
        }
    }
}