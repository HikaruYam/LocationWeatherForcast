package com.example.locationweatherforcast.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.locationweatherforcast.data.database.AppDatabase
import com.example.locationweatherforcast.data.database.FavoriteLocationDao
import com.example.locationweatherforcast.data.database.WeatherCacheDao
import com.example.locationweatherforcast.data.repository.FavoriteLocationRepository
import com.example.locationweatherforcast.data.repository.FavoriteLocationRepositoryImpl
import com.example.locationweatherforcast.data.repository.WeatherCacheRepository
import com.example.locationweatherforcast.data.repository.WeatherCacheRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Database migration from version 1 to 2
     * Adds weather_cache table for caching weather data
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            try {
                // Create weather_cache table with comprehensive schema
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS weather_cache (
                        locationKey TEXT NOT NULL,
                        date TEXT NOT NULL,
                        weatherCode INTEGER NOT NULL,
                        temperatureMax REAL NOT NULL,
                        temperatureMin REAL NOT NULL,
                        precipitation REAL NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        locationName TEXT,
                        cachedAt TEXT NOT NULL,
                        expiresAt TEXT NOT NULL,
                        lastUpdated TEXT NOT NULL,
                        PRIMARY KEY(locationKey, date)
                    )
                """.trimIndent())
                
                // Create index for performance optimization
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_weather_cache_cachedAt 
                    ON weather_cache(cachedAt)
                """.trimIndent())
                
                // Create index for cleanup operations
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_weather_cache_expiresAt 
                    ON weather_cache(expiresAt)
                """.trimIndent())
                
            } catch (e: Exception) {
                // Migration failed - this should be logged for monitoring
                throw e // Re-throw to trigger fallback strategy
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "weather_app_database"
        )
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration() // Fallback if migration fails
        .build()
    }
    
    @Provides
    fun provideFavoriteLocationDao(database: AppDatabase): FavoriteLocationDao {
        return database.favoriteLocationDao()
    }
    
    @Provides
    fun provideWeatherCacheDao(database: AppDatabase): WeatherCacheDao {
        return database.weatherCacheDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindFavoriteLocationRepository(
        favoriteLocationRepositoryImpl: FavoriteLocationRepositoryImpl
    ): FavoriteLocationRepository
    
    @Binds
    @Singleton
    abstract fun bindWeatherCacheRepository(
        weatherCacheRepositoryImpl: WeatherCacheRepositoryImpl
    ): WeatherCacheRepository
}