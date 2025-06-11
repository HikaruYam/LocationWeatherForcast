package com.example.locationweatherforcast.di

import android.content.Context
import androidx.room.Room
import com.example.locationweatherforcast.data.database.AppDatabase
import com.example.locationweatherforcast.data.database.FavoriteLocationDao
import com.example.locationweatherforcast.data.repository.FavoriteLocationRepository
import com.example.locationweatherforcast.data.repository.FavoriteLocationRepositoryImpl
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
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "weather_app_database"
        ).build()
    }
    
    @Provides
    fun provideFavoriteLocationDao(database: AppDatabase): FavoriteLocationDao {
        return database.favoriteLocationDao()
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
}