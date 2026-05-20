package com.citizenconnect.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onResult: (Double, Double, String) -> Unit, onError: (() -> Unit)? = null) {
        Log.d("LocationHelper", "Requesting current location...")
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("LocationHelper", "Location found: ${location.latitude}, ${location.longitude}")
                    val address = getAddress(location.latitude, location.longitude)
                    onResult(location.latitude, location.longitude, address)
                } else {
                    Log.d("LocationHelper", "Current location is null, trying last known location...")
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            Log.d("LocationHelper", "Last known location found: ${lastLoc.latitude}, ${lastLoc.longitude}")
                            onResult(lastLoc.latitude, lastLoc.longitude, getAddress(lastLoc.latitude, lastLoc.longitude))
                        } else {
                            Log.d("LocationHelper", "Both current and last known location are null")
                            onError?.invoke()
                        }
                    }.addOnFailureListener { e ->
                        Log.e("LocationHelper", "Error getting last location", e)
                        onError?.invoke()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationHelper", "Error getting current location", e)
                onError?.invoke()
            }
    }

    fun getAddress(lat: Double, lng: Double): String = try {
        Log.d("LocationHelper", "Getting address for $lat, $lng")
        @Suppress("DEPRECATION")
        val addressList = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lng, 1)
        val address = addressList?.firstOrNull()?.getAddressLine(0)
            ?: "${"%.4f".format(lat)}, ${"%.4f".format(lng)}"
        Log.d("LocationHelper", "Address: $address")
        address
    } catch (e: Exception) {
        Log.e("LocationHelper", "Geocoder error", e)
        "${"%.4f".format(lat)}, ${"%.4f".format(lng)}"
    }
}
