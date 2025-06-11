package com.example.locationweatherforcast.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [FavoriteLocation::class, WeatherCache::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun favoriteLocationDao(): FavoriteLocationDao
    abstract fun weatherCacheDao(): WeatherCacheDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}