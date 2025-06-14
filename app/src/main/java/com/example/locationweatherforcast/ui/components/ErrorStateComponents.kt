package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Generic error state component with customizable icon and actions
 */
@Composable
fun ErrorStateComponent(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Delete,
    primaryAction: ErrorAction? = null,
    secondaryAction: ErrorAction? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Error icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                primaryAction?.let { action ->
                    Button(
                        onClick = action.onClick,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(action.text)
                    }
                }
                
                secondaryAction?.let { action ->
                    OutlinedButton(
                        onClick = action.onClick,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(action.text)
                    }
                }
            }
        }
    }
}

/**
 * Data class for error actions
 */
data class ErrorAction(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

/**
 * Network error component
 */
@Composable
fun NetworkErrorComponent(
    onRetry: () -> Unit,
    onSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorStateComponent(
        title = "インターネット接続エラー",
        message = "インターネット接続を確認してください。ネットワークに接続されていないか、接続が不安定な可能性があります。",
        icon = Icons.Default.Refresh,
        primaryAction = ErrorAction(
            text = "再試行",
            icon = Icons.Default.Refresh,
            onClick = onRetry
        ),
        secondaryAction = onSettings?.let { 
            ErrorAction(
                text = "設定を開く",
                icon = Icons.Default.Settings,
                onClick = it
            )
        },
        modifier = modifier
    )
}

/**
 * Location permission error component
 */
@Composable
fun LocationPermissionErrorComponent(
    onRequestPermission: () -> Unit,
    onManualLocation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorStateComponent(
        title = "位置情報の許可が必要です",
        message = "現在地の天気を表示するために、位置情報へのアクセス許可が必要です。設定から許可してください。",
        icon = Icons.Default.LocationOn,
        primaryAction = ErrorAction(
            text = "許可する",
            icon = Icons.Default.LocationOn,
            onClick = onRequestPermission
        ),
        secondaryAction = onManualLocation?.let {
            ErrorAction(
                text = "手動で場所を設定",
                icon = Icons.Default.Edit,
                onClick = it
            )
        },
        modifier = modifier
    )
}

/**
 * GPS disabled error component
 */
@Composable
fun GpsDisabledErrorComponent(
    onEnableGps: () -> Unit,
    onManualLocation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorStateComponent(
        title = "GPS が無効です",
        message = "位置情報サービスが無効になっています。設定でGPSを有効にしてください。",
        icon = Icons.Default.LocationOn,
        primaryAction = ErrorAction(
            text = "GPS設定を開く",
            icon = Icons.Default.LocationOn,
            onClick = onEnableGps
        ),
        secondaryAction = onManualLocation?.let {
            ErrorAction(
                text = "手動で場所を設定",
                icon = Icons.Default.Edit,
                onClick = it
            )
        },
        modifier = modifier
    )
}

/**
 * API error component
 */
@Composable
fun ApiErrorComponent(
    message: String = "サーバーからデータを取得できませんでした",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorStateComponent(
        title = "サービス一時停止中",
        message = "$message\n\nしばらく待ってから再度お試しください。",
        icon = Icons.Default.Refresh,
        primaryAction = ErrorAction(
            text = "再試行",
            icon = Icons.Default.Refresh,
            onClick = onRetry
        ),
        modifier = modifier
    )
}

/**
 * Empty state component for no favorite locations
 */
@Composable
fun EmptyFavoritesComponent(
    onAddCurrentLocation: () -> Unit,
    onAddManualLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Empty state icon
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = "お気に入りの場所がありません",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = "天気を確認したい場所を追加して、いつでも簡単にチェックできるようにしましょう。",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddCurrentLocation,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("現在地を追加")
                }
                
                OutlinedButton(
                    onClick = onAddManualLocation,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("場所を手動で追加")
                }
            }
        }
    }
}

/**
 * Generic empty state component
 */
@Composable
fun EmptyStateComponent(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Add,
    actionButton: ErrorAction? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Empty state icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action button
            actionButton?.let { action ->
                Button(
                    onClick = action.onClick,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(action.text)
                }
            }
        }
    }
}

/**
 * Offline mode indicator
 */
@Composable
fun OfflineModeIndicator(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "オフラインモード - キャッシュされたデータを表示",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}