package com.example.locationweatherforcast

import android.app.Application
import com.example.locationweatherforcast.data.database.AppDatabase

class WeatherApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
    }
}