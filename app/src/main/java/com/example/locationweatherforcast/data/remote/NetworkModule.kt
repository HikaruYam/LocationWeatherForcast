package com.example.locationweatherforcast.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Network module for setting up Retrofit and API services
 */
object NetworkModule {
    private const val BASE_URL = "https://api.open-meteo.com/"
    
    /**
     * JSON configuration for kotlinx.serialization
     */
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    /**
     * OkHttpClient with logging and timeout configurations
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    
    /**
     * Retrofit instance configured with OkHttpClient and KotlinX Serialization
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    
    /**
     * Provides the WeatherApiService instance
     */
    val weatherApiService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
