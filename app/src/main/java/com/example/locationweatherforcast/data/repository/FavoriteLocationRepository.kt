package com.example.locationweatherforcast.data.repository

import com.example.locationweatherforcast.data.database.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface FavoriteLocationRepository {
    
    fun getAllLocations(): Flow<List<FavoriteLocation>>
    
    suspend fun addLocation(location: FavoriteLocation): Result<Unit>
    
    suspend fun deleteLocation(id: String): Result<Unit>
    
    suspend fun updateLocationOrder(locations: List<FavoriteLocation>): Result<Unit>
    
    suspend fun getLocationCount(): Int
    
    suspend fun getLocationById(id: String): FavoriteLocation?
}