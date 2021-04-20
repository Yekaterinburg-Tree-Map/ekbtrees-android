package ru.ekbtrees.treemap.ui

import com.google.gson.annotations.SerializedName

class Tree {
    @SerializedName("Id")
    val id = 0
    @SerializedName("Latitude")
    val latitude: Double = 0.0
    @SerializedName("Longitude")
    val longitude: Double = 0.0
    @SerializedName("DiameterOfCrown")
    val diameterOfCrown: Double = 0.0
    @SerializedName("Type")
    val type: String? = null
}