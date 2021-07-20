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

data class TreeDetailDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("geographicalPoint")
    val coord: LatLonDto,
    @SerializedName("species")
    val species: SpeciesDto,
    @SerializedName("treeHeight")
    val height: Double?,
    @SerializedName("numberOfTreeTrunks")
    val numberOfTrunks: Int?,
    @SerializedName("trunkGirth")
    val trunkGirth: Double?,
    @SerializedName("diameterOfCrown")
    val diameterOfCrown: Double?,
    @SerializedName("heightOfTheFirstBranch")
    val heightOfTheFirstBranch: Double?,
    @SerializedName("conditionAssessment")
    val conditionAssessment: Int?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("treePlantingType")
    val treePlantingType: String?,
    @SerializedName("created")
    val createTime: String?,
    @SerializedName("updated")
    val updateTime: String?,
    @SerializedName("authorId")
    val authorId: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("fileIds")
    val fileIds: List<Int>?
)