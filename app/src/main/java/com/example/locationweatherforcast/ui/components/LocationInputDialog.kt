package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LocationInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var locationName by remember { mutableStateOf("") }
    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }
    var isLatitudeError by remember { mutableStateOf(false) }
    var isLongitudeError by remember { mutableStateOf(false) }

    fun validateInput(): Boolean {
        var isValid = true
        
        // Validate name
        if (locationName.isBlank()) {
            isNameError = true
            isValid = false
        } else {
            isNameError = false
        }
        
        // Validate latitude
        val latitude = latitudeText.toDoubleOrNull()
        if (latitude == null || latitude < -90 || latitude > 90) {
            isLatitudeError = true
            isValid = false
        } else {
            isLatitudeError = false
        }
        
        // Validate longitude
        val longitude = longitudeText.toDoubleOrNull()
        if (longitude == null || longitude < -180 || longitude > 180) {
            isLongitudeError = true
            isValid = false
        } else {
            isLongitudeError = false
        }
        
        return isValid
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "場所を追加",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Location name input
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { 
                        locationName = it
                        isNameError = false
                    },
                    label = { Text("場所名") },
                    placeholder = { Text("例: 東京駅、自宅など") },
                    isError = isNameError,
                    supportingText = if (isNameError) {
                        { Text("場所名を入力してください") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Coordinates section
                Text(
                    text = "座標情報",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Latitude input
                OutlinedTextField(
                    value = latitudeText,
                    onValueChange = { 
                        latitudeText = it
                        isLatitudeError = false
                    },
                    label = { Text("緯度") },
                    placeholder = { Text("例: 35.681236") },
                    isError = isLatitudeError,
                    supportingText = if (isLatitudeError) {
                        { Text("有効な緯度を入力してください（-90〜90）") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Longitude input
                OutlinedTextField(
                    value = longitudeText,
                    onValueChange = { 
                        longitudeText = it
                        isLongitudeError = false
                    },
                    label = { Text("経度") },
                    placeholder = { Text("例: 139.767125") },
                    isError = isLongitudeError,
                    supportingText = if (isLongitudeError) {
                        { Text("有効な経度を入力してください（-180〜180）") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Help text
                Text(
                    text = "※ 座標は Google Maps などで調べることができます",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss
                    ) {
                        Text("キャンセル")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (validateInput()) {
                                val latitude = latitudeText.toDouble()
                                val longitude = longitudeText.toDouble()
                                onConfirm(locationName.trim(), latitude, longitude)
                            }
                        }
                    ) {
                        Text("追加")
                    }
                }
            }
        }
    }
}

@Composable
fun QuickLocationDialog(
    onDismiss: () -> Unit,
    onSelectLocation: (name: String, latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val popularLocations = listOf(
        Triple("東京駅", 35.681236, 139.767125),
        Triple("大阪駅", 34.702485, 135.495951),
        Triple("名古屋駅", 35.170915, 136.881537),
        Triple("福岡天神", 33.590355, 130.399533),
        Triple("札幌駅", 43.068661, 141.350755),
        Triple("仙台駅", 38.260559, 140.882347)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "人気の場所から選択",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "よく使われる場所から選択できます",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                popularLocations.forEach { (name, lat, lng) ->
                    OutlinedButton(
                        onClick = {
                            onSelectLocation(name, lat, lng)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name)
                            Text(
                                text = "${String.format("%.3f", lat)}, ${String.format("%.3f", lng)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("閉じる")
            }
        }
    )
}