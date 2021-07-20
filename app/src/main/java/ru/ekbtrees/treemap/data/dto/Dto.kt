package ru.ekbtrees.treemap.data.dto

import com.google.gson.annotations.SerializedName

data class ClusterTreesDto(
    @SerializedName("centre")
    val coord: LatLonDto,
    @SerializedName("count")
    val count: Int
)

data class MapTreeDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("geographicalPoint")
    val coord: LatLonDto,
    @SerializedName("diameterOfCrown")
    val diameter: Float,
    @SerializedName("species")
    val species: SpeciesDto
)

data class SpeciesDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val name: String
)

data class LatLonDto(
    @SerializedName("latitude")
    val lat: Double,
    @SerializedName("longitude")
    val lon: Double
)