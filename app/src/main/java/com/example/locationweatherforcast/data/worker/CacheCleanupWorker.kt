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
            if (stats.totalEntries > 1000) {
                // TODO: Implement old cache cleanup logic
                // For now, just clean expired entries
            }
            
            Result.success()
        } catch (exception: Exception) {
            // Log error and retry if possible
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    companion object {
        const val WORK_NAME = "cache_cleanup_work"
    }
}