package com.example.locationweatherforcast.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.locationweatherforcast.data.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service for handling location-related operations
 */
@Singleton
class LocationService @Inject constructor(@ApplicationContext private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    // Performance: Location request timeout to prevent hanging requests
    private val locationTimeoutMs = 15_000L // 15 seconds
    
    // Memory leak prevention: Cache for location manager to avoid repeated system service calls
    private val locationManager by lazy { 
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager 
    }
    
    /**
     * Check if location permissions are granted
     * @return true if permissions are granted, false otherwise
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if location services are enabled
     * @return true if location services are enabled, false otherwise
     */
    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    /**
     * Get the current location
     * @return LocationData containing latitude and longitude
     * @throws SecurityException if location permissions are not granted
     * @throws LocationDisabledException if location services are disabled
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }
        
        if (!isLocationEnabled()) {
            throw LocationDisabledException("Location services are disabled")
        }
        
        // Performance & Memory: Add timeout to prevent hanging location requests
        return withTimeout(locationTimeoutMs) {
            suspendCancellableCoroutine { continuation ->
                val cancellationToken = CancellationTokenSource()
                
                // Memory leak prevention: Proper cleanup on cancellation
                continuation.invokeOnCancellation {
                    try {
                        cancellationToken.cancel()
                    } catch (e: Exception) {
                        // Ignore cleanup errors
                    }
                }
                
                val locationTask = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                )
                
                locationTask.addOnSuccessListener { location: Location? ->
                    if (continuation.isActive) {
                        if (location != null) {
                            continuation.resume(
                                LocationData(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        } else {
                            continuation.resumeWithException(
                                LocationNotFoundException("Could not get current location")
                            )
                        }
                    }
                }.addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }
            }
        }
    }
    
    /**
     * Performance: Get location with custom timeout
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationWithTimeout(timeoutMs: Long): LocationData {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }
        
        if (!isLocationEnabled()) {
            throw LocationDisabledException("Location services are disabled")
        }
        
        return withTimeout(timeoutMs) {
            suspendCancellableCoroutine { continuation ->
                val cancellationToken = CancellationTokenSource()
                
                continuation.invokeOnCancellation {
                    try {
                        cancellationToken.cancel()
                    } catch (e: Exception) {
                        // Ignore cleanup errors
                    }
                }
                
                val locationTask = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Use balanced priority for custom timeout
                    cancellationToken.token
                )
                
                locationTask.addOnSuccessListener { location: Location? ->
                    if (continuation.isActive) {
                        if (location != null) {
                            continuation.resume(
                                LocationData(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        } else {
                            continuation.resumeWithException(
                                LocationNotFoundException("Could not get current location")
                            )
                        }
                    }
                }.addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }
            }
        }
    }
}

/**
 * Exception thrown when location services are disabled
 */
class LocationDisabledException(message: String) : Exception(message)

/**
 * Exception thrown when location could not be found
 */
class LocationNotFoundException(message: String) : Exception(message)
