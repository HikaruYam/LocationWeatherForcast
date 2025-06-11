package com.example.locationweatherforcast.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {
    
    @Query("SELECT * FROM favorite_locations ORDER BY `order` ASC")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    
    @Query("SELECT * FROM favorite_locations WHERE id = :id")
    suspend fun getFavoriteLocationById(id: String): FavoriteLocation?
    
    @Query("SELECT COUNT(*) FROM favorite_locations")
    suspend fun getCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(location: FavoriteLocation)
    
    @Update
    suspend fun updateFavoriteLocation(location: FavoriteLocation)
    
    @Delete
    suspend fun deleteFavoriteLocation(location: FavoriteLocation)
    
    @Query("DELETE FROM favorite_locations WHERE id = :id")
    suspend fun deleteFavoriteLocationById(id: String)
    
    @Query("DELETE FROM favorite_locations")
    suspend fun deleteAllFavoriteLocations()
    
    @Query("UPDATE favorite_locations SET `order` = :newOrder WHERE id = :id")
    suspend fun updateOrder(id: String, newOrder: Int)
}