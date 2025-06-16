package com.example.locationweatherforcast.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import kotlinx.coroutines.delay

/**
 * Accessibility-focused heading component with proper semantics
 */
@Composable
fun AccessibleHeading(
    text: String,
    modifier: Modifier = Modifier,
    level: Int = 1,
    color: Color = LocalContentColor.current
) {
    Text(
        text = text,
        style = when (level) {
            1 -> MaterialTheme.typography.headlineLarge
            2 -> MaterialTheme.typography.headlineMedium
            3 -> MaterialTheme.typography.headlineSmall
            4 -> MaterialTheme.typography.titleLarge
            5 -> MaterialTheme.typography.titleMedium
            else -> MaterialTheme.typography.titleSmall
        },
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier.semantics {
            heading()
            contentDescription = "見出し: $text"
        }
    )
}

/**
 * Live region for announcing dynamic content changes to screen readers
 */
@Composable
fun AccessibilityAnnouncement(
    message: String,
    mode: LiveRegionMode = LiveRegionMode.Polite,
    modifier: Modifier = Modifier
) {
    var announcement by remember { mutableStateOf("") }
    
    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            announcement = message
            // Clear after a short delay to allow for re-announcement of the same message
            delay(100)
            announcement = ""
        }
    }
    
    Box(
        modifier = modifier.semantics {
            liveRegion = mode
            contentDescription = announcement
        }
    )
}

/**
 * Component for managing focus on screen changes
 */
@Composable
fun FocusManagementContainer(
    shouldRequestFocus: Boolean = false,
    focusMessage: String = "",
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(shouldRequestFocus) {
        if (shouldRequestFocus) {
            delay(100) // Small delay to ensure content is composed
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {
                // Focus request might fail, but that's okay
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .semantics {
                if (focusMessage.isNotEmpty()) {
                    contentDescription = focusMessage
                }
            }
    ) {
        content()
    }
}

/**
 * Skip link for keyboard users to skip repetitive content
 */
@Composable
fun SkipToContentLink(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    // This would typically be implemented as an invisible button that becomes visible on focus
    // For now, we'll create a semantic marker that screen readers can use
    Box(
        modifier = modifier
            .semantics {
                contentDescription = "メインコンテンツにスキップするにはエンターキーを押してください"
            }
            .focusable()
    ) {
        // Invisible content for screen readers
        Text(
            text = "",
            modifier = Modifier.padding(0.dp)
        )
    }
}

/**
 * High contrast indicator for accessibility
 */
@Composable
fun HighContrastSupportIndicator(
    isHighContrast: Boolean,
    modifier: Modifier = Modifier
) {
    if (isHighContrast) {
        Box(
            modifier = modifier.semantics {
                contentDescription = "ハイコントラストモードが有効です"
                liveRegion = LiveRegionMode.Polite
            }
        )
    }
}

/**
 * Screen reader optimized error message component
 */
@Composable
fun AccessibleErrorMessage(
    error: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.semantics {
            contentDescription = "エラー: $error"
            liveRegion = LiveRegionMode.Assertive
        }
    ) {
        ErrorStateComponent(
            title = "エラーが発生しました",
            message = error,
            primaryAction = onRetry?.let { retry ->
                ErrorAction(
                    text = "再試行",
                    icon = Icons.Default.Refresh,
                    onClick = retry
                )
            }
        )
    }
}

/**
 * Loading state with proper accessibility announcements
 */
@Composable
fun AccessibleLoadingState(
    message: String = "読み込み中です",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.semantics {
            contentDescription = message
            liveRegion = LiveRegionMode.Polite
        }
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}