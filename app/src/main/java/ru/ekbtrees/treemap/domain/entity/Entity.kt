package ru.ekbtrees.treemap.domain.entity

import androidx.annotation.ColorInt
import com.google.android.gms.maps.model.LatLng

data class ClusterTreesEntity(val count: Int, val coord: LatLonEntity)

data class TreeEntity(val id: String, val diameter: Float, val species: SpeciesEntity, val coord: LatLonEntity)

data class SpeciesEntity(val id: String, @ColorInt val color: Int, val name: String)

data class LatLonEntity(val lat: Double, val lon: Double) {
    fun asLatLng(): LatLng = LatLng(lat, lon)
}

