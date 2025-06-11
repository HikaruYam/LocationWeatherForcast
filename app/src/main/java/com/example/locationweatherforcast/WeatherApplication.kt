package com.example.locationweatherforcast

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.locationweatherforcast.data.worker.CacheCleanupWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerConfiguration: Configuration
    
    override fun onCreate() {
        super.onCreate()
        setupCacheCleanupWorker()
    }
    
    override val workManagerConfiguration: Configuration
        get() = workerConfiguration
    
    private fun setupCacheCleanupWorker() {
        val cacheCleanupRequest = PeriodicWorkRequestBuilder<CacheCleanupWorker>(
            repeatInterval = 6, // Run every 6 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CacheCleanupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cacheCleanupRequest
        )
    }
}