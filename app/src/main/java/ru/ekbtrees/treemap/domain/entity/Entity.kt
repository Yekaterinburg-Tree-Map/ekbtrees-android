package ru.ekbtrees.treemap.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ClusterTreesEntity(val count: Int, val coord: LatLonEntity)
@Parcelize
data class TreeEntity(
    val id: String,
    val diameter: Float,
    val species: SpeciesEntity,
    val coord: LatLonEntity
) : Parcelable

data class TreeDetailEntity(
    val id: String,
    val coord: LatLonEntity,
    val species: SpeciesEntity,
    val height: Double,
    val numberOfTrunks: Int,
    val trunkGirth: Double,
    val diameterOfCrown: Int,
    val heightOfTheFirstBranch: Double,
    val conditionAssessment: Int,
    val age: Int,
    val treePlantingType: String,
    val createTime: String,
    val updateTime: String,
    val authorId: Int,
    val status: String,
    val fileIds: Collection<Int>
)
@Parcelize
data class SpeciesEntity(val id: String, val color: Int, val name: String) : Parcelable
@Parcelize
data class LatLonEntity(val lat: Double, val lon: Double) : Parcelable

