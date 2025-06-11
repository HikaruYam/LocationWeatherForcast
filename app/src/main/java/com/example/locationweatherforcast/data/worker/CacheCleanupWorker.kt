package com.example.locationweatherforcast.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.locationweatherforcast.data.repository.WeatherCacheRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker for cleaning up expired weather cache entries
 */
@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherCacheRepository: WeatherCacheRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Clean up expired cache entries
            weatherCacheRepository.cleanupExpiredCache()
            
            // Get cache stats and perform cleanup if needed
            val stats = weatherCacheRepository.getCacheStats()
            
            // If cache is too large (> 1000 entries), clean up oldest entries
            if (stats.totalEntries > MAX_CACHE_ENTRIES) {
                // Clear all cache to prevent memory issues
                weatherCacheRepository.clearAllCache()
            }
            
            // Log successful cleanup for monitoring
            Result.success()
        } catch (exception: Exception) {
            // Enhanced error handling with specific retry logic
            return when {
                exception is OutOfMemoryError -> {
                    // Critical error - clear cache immediately
                    try {
                        weatherCacheRepository.clearAllCache()
                        Result.success()
                    } catch (e: Exception) {
                        Result.failure()
                    }
                }
                runAttemptCount < MAX_RETRY_ATTEMPTS -> {
                    // Transient error - retry with exponential backoff
                    Result.retry()
                }
                else -> {
                    // Max retries exceeded
                    Result.failure()
                }
            }
        }
    }
    
    companion object {
        const val WORK_NAME = "cache_cleanup_work"
        private const val MAX_CACHE_ENTRIES = 1000
        private const val MAX_RETRY_ATTEMPTS = 3
    }
}