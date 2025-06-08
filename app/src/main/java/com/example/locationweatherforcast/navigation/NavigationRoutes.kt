package com.example.locationweatherforcast.navigation

object NavigationRoutes {
    const val CURRENT_WEATHER = "current_weather"
    const val FAVORITE_LOCATIONS = "favorite_locations"
    const val LOCATION_DETAIL = "location_detail/{locationId}"
    
    fun locationDetail(locationId: String): String {
        return "location_detail/$locationId"
    }
}