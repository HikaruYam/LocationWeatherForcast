package com.example.locationweatherforcast.data.monitoring

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring utility for tracking app performance metrics
 */
@Singleton
class PerformanceMonitor @Inject constructor() {
    
    private val _metrics = MutableStateFlow(PerformanceMetrics())
    val metrics: StateFlow<PerformanceMetrics> = _metrics.asStateFlow()
    
    // Cache for ongoing operations
    private val ongoingOperations = mutableMapOf<String, Long>()
    
    /**
     * Start tracking an operation
     */
    fun startOperation(operationName: String) {
        ongoingOperations[operationName] = System.currentTimeMillis()
    }
    
    /**
     * End tracking an operation and record the duration
     */
    fun endOperation(operationName: String, success: Boolean = true) {
        val startTime = ongoingOperations.remove(operationName)
        if (startTime != null) {
            val duration = System.currentTimeMillis() - startTime
            recordOperationDuration(operationName, duration, success)
        }
    }
    
    /**
     * Record operation duration
     */
    private fun recordOperationDuration(operationName: String, durationMs: Long, success: Boolean) {
        val currentMetrics = _metrics.value
        val updatedMetrics = when (operationName) {
            "weather_api_call" -> {
                currentMetrics.copy(
                    totalApiCalls = currentMetrics.totalApiCalls + 1,
                    successfulApiCalls = if (success) currentMetrics.successfulApiCalls + 1 else currentMetrics.successfulApiCalls,
                    averageApiResponseTime = calculateNewAverage(
                        currentMetrics.averageApiResponseTime,
                        currentMetrics.totalApiCalls,
                        durationMs
                    ),
                    slowestApiCall = maxOf(currentMetrics.slowestApiCall, durationMs)
                )
            }
            "location_fetch" -> {
                currentMetrics.copy(
                    totalLocationRequests = currentMetrics.totalLocationRequests + 1,
                    successfulLocationRequests = if (success) currentMetrics.successfulLocationRequests + 1 else currentMetrics.successfulLocationRequests,
                    averageLocationTime = calculateNewAverage(
                        currentMetrics.averageLocationTime,
                        currentMetrics.totalLocationRequests,
                        durationMs
                    )
                )
            }
            "database_query" -> {
                currentMetrics.copy(
                    totalDatabaseQueries = currentMetrics.totalDatabaseQueries + 1,
                    averageDatabaseQueryTime = calculateNewAverage(
                        currentMetrics.averageDatabaseQueryTime,
                        currentMetrics.totalDatabaseQueries,
                        durationMs
                    )
                )
            }
            "screen_render" -> {
                currentMetrics.copy(
                    totalScreenRenders = currentMetrics.totalScreenRenders + 1,
                    averageScreenRenderTime = calculateNewAverage(
                        currentMetrics.averageScreenRenderTime,
                        currentMetrics.totalScreenRenders,
                        durationMs
                    )
                )
            }
            else -> currentMetrics
        }
        
        _metrics.value = updatedMetrics
        
        // Log slow operations
        if (durationMs > getSlowThreshold(operationName)) {
            Log.w("PerformanceMonitor", "Slow operation detected: $operationName took ${durationMs}ms")
        }
    }
    
    /**
     * Calculate new running average
     */
    private fun calculateNewAverage(currentAverage: Long, totalCount: Int, newValue: Long): Long {
        return if (totalCount <= 1) {
            newValue
        } else {
            ((currentAverage * (totalCount - 1)) + newValue) / totalCount
        }
    }
    
    /**
     * Get slow operation threshold for different operation types
     */
    private fun getSlowThreshold(operationName: String): Long {
        return when (operationName) {
            "weather_api_call" -> 5000L // 5 seconds
            "location_fetch" -> 10000L // 10 seconds
            "database_query" -> 500L // 500ms
            "screen_render" -> 1000L // 1 second
            else -> 2000L // 2 seconds default
        }
    }
    
    /**
     * Record memory usage
     */
    fun recordMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        val currentMetrics = _metrics.value
        _metrics.value = currentMetrics.copy(
            currentMemoryUsage = usedMemory,
            peakMemoryUsage = maxOf(currentMetrics.peakMemoryUsage, usedMemory)
        )
        
        // Log high memory usage
        val memoryUsageMB = usedMemory / (1024 * 1024)
        if (memoryUsageMB > 100) { // Alert if over 100MB
            Log.w("PerformanceMonitor", "High memory usage detected: ${memoryUsageMB}MB")
        }
    }
    
    /**
     * Record cache statistics
     */
    fun recordCacheStats(hitCount: Int, missCount: Int, evictionCount: Int) {
        val currentMetrics = _metrics.value
        _metrics.value = currentMetrics.copy(
            cacheHitCount = hitCount,
            cacheMissCount = missCount,
            cacheEvictionCount = evictionCount,
            cacheHitRate = if ((hitCount + missCount) > 0) {
                (hitCount.toFloat() / (hitCount + missCount)) * 100
            } else 0f
        )
    }
    
    /**
     * Get performance summary
     */
    fun getPerformanceSummary(): String {
        val metrics = _metrics.value
        return buildString {
            appendLine("=== Performance Summary ===")
            appendLine("API Calls: ${metrics.successfulApiCalls}/${metrics.totalApiCalls} successful")
            appendLine("Avg API Response: ${metrics.averageApiResponseTime}ms")
            appendLine("Location Requests: ${metrics.successfulLocationRequests}/${metrics.totalLocationRequests} successful")
            appendLine("Avg Location Time: ${metrics.averageLocationTime}ms")
            appendLine("Database Queries: ${metrics.totalDatabaseQueries}")
            appendLine("Avg DB Query Time: ${metrics.averageDatabaseQueryTime}ms")
            appendLine("Memory Usage: ${metrics.currentMemoryUsage / (1024 * 1024)}MB")
            appendLine("Peak Memory: ${metrics.peakMemoryUsage / (1024 * 1024)}MB")
            appendLine("Cache Hit Rate: ${String.format("%.1f", metrics.cacheHitRate)}%")
        }
    }
    
    /**
     * Reset metrics
     */
    fun resetMetrics() {
        _metrics.value = PerformanceMetrics()
        ongoingOperations.clear()
    }
}

/**
 * Data class holding performance metrics
 */
data class PerformanceMetrics(
    // API Performance
    val totalApiCalls: Int = 0,
    val successfulApiCalls: Int = 0,
    val averageApiResponseTime: Long = 0L,
    val slowestApiCall: Long = 0L,
    
    // Location Performance
    val totalLocationRequests: Int = 0,
    val successfulLocationRequests: Int = 0,
    val averageLocationTime: Long = 0L,
    
    // Database Performance
    val totalDatabaseQueries: Int = 0,
    val averageDatabaseQueryTime: Long = 0L,
    
    // UI Performance
    val totalScreenRenders: Int = 0,
    val averageScreenRenderTime: Long = 0L,
    
    // Memory Performance
    val currentMemoryUsage: Long = 0L,
    val peakMemoryUsage: Long = 0L,
    
    // Cache Performance
    val cacheHitCount: Int = 0,
    val cacheMissCount: Int = 0,
    val cacheEvictionCount: Int = 0,
    val cacheHitRate: Float = 0f
)