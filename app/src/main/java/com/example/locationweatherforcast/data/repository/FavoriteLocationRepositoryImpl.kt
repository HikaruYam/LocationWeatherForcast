package com.example.locationweatherforcast.data.repository

import com.example.locationweatherforcast.data.database.FavoriteLocation
import com.example.locationweatherforcast.data.database.FavoriteLocationDao
import kotlinx.coroutines.flow.Flow
class FavoriteLocationRepositoryImpl(
    private val favoriteLocationDao: FavoriteLocationDao
) : FavoriteLocationRepository {
    
    companion object {
        private const val MAX_FAVORITE_LOCATIONS = 5
    }
    
    override fun getAllLocations(): Flow<List<FavoriteLocation>> {
        return favoriteLocationDao.getAllFavoriteLocations()
    }
    
    override suspend fun addLocation(location: FavoriteLocation): Result<Unit> {
        return try {
            val currentCount = favoriteLocationDao.getCount()
            if (currentCount >= MAX_FAVORITE_LOCATIONS) {
                Result.failure(Exception("お気に入り場所は最大${MAX_FAVORITE_LOCATIONS}箇所までです"))
            } else {
                val newOrder = currentCount
                val locationWithOrder = location.copy(order = newOrder)
                favoriteLocationDao.insertFavoriteLocation(locationWithOrder)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteLocation(id: String): Result<Unit> {
        return try {
            val locationToDelete = favoriteLocationDao.getFavoriteLocationById(id)
                ?: return Result.failure(Exception("削除対象の場所が見つかりません"))
            
            favoriteLocationDao.deleteFavoriteLocationById(id)
            
            reorderAfterDeletion(locationToDelete.order)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateLocationOrder(locations: List<FavoriteLocation>): Result<Unit> {
        return try {
            locations.forEachIndexed { index, location ->
                val updatedLocation = location.copy(order = index)
                favoriteLocationDao.updateFavoriteLocation(updatedLocation)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLocationCount(): Int {
        return try {
            favoriteLocationDao.getCount()
        } catch (e: Exception) {
            0
        }
    }
    
    override suspend fun getLocationById(id: String): FavoriteLocation? {
        return try {
            favoriteLocationDao.getFavoriteLocationById(id)
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun reorderAfterDeletion(deletedOrder: Int) {
        try {
            val allLocations = favoriteLocationDao.getAllFavoriteLocations()
            allLocations.collect { locations ->
                locations.filter { it.order > deletedOrder }
                    .forEach { location ->
                        favoriteLocationDao.updateOrder(
                            location.id, 
                            location.order - 1
                        )
                    }
                return@collect
            }
        } catch (e: Exception) {
            // 並び替えエラーは無視（データ整合性は保たれる）
        }
    }
}