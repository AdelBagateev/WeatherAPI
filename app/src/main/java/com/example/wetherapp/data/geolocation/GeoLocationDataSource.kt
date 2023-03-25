
package com.example.wetherapp.data.geolocation

import android.annotation.SuppressLint
import com.example.wetherapp.domain.geolocation.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await

 class GeoLocationDataSource(
    private val client: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation() : GeoLocation = client.lastLocation.await().let {
        GeoLocation(
            it.longitude,
            it.latitude
        )
    }
}
