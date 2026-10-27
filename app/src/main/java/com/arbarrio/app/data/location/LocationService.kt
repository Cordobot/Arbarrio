package com.arbarrio.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class LocationService(private val context: Context) {
    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    fun getCurrentLocationName(): Flow<String?> = callbackFlow {
        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val cityName = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
                        val countryName = addresses?.firstOrNull()?.countryName
                        
                        val result = if (cityName != null) {
                            "$cityName, ${countryName ?: ""}".trimEnd(',', ' ')
                        } else {
                            null
                        }
                        trySend(result)
                    } else {
                        trySend(null)
                    }
                }
                .addOnFailureListener {
                    trySend(null)
                }
        } catch (e: Exception) {
            trySend(null)
        }
        awaitClose { }
    }
}
