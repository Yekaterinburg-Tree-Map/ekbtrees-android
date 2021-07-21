package ru.ekbtrees.treemap.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task

/**
 * Класс для предостовления местоположения.
 * */
class LocationProvider(private val context: Context) : LocationSource {

    val lastLocation: LatLng
        get() = LatLng(_lastLocation.latitude, _lastLocation.longitude)
    private lateinit var _lastLocation: Location

    private var locationListener: LocationSource.OnLocationChangedListener? = null
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 5000
        fastestInterval = 16 //16 ms = 60 fps
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                _lastLocation = location
                locationListener?.onLocationChanged(location)
            }
        }
    }

    /**
     * Предоставляет местоположение пользователя
     * @throws IllegalAccessException Пользователь не предоставил разрешение на отслеживание местоположения
     * */
    fun fetchUserLocation(): Task<Location> {
        if (checkLocationPermission()) {
            return fusedLocationProviderClient.lastLocation
        }
        throw IllegalAccessException("You have to request location permission!")
    }

    fun startLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        locationListener = listener
    }

    override fun deactivate() {
        locationListener = null
    }
}