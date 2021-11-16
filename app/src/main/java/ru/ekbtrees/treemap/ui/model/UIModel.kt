package ru.ekbtrees.treemap.ui.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

data class RegionBoundsUIModel(val topLeft: LatLng, val bottomRight: LatLng)

@Parcelize
data class NewTreeDetailUIModel(
    val coord: LatLng,
    val species: SpeciesUIModel?,
    val height: Double?,
    val numberOfTrunks: Int?,
    val trunkGirth: Double?,
    val diameterOfCrown: Double,
    val heightOfTheFirstBranch: Double?,
    val conditionAssessment: Int?,
    val age: Int?,
    val treePlantingType: String?,
    val createTime: String,
    val updateTime: String,
    val authorId: Int?,
    val status: String?,
    val fileIds: List<Int>
) : Parcelable

@Parcelize
data class TreeDetailUIModel(
    val id: String,
    val coord: LatLng,
    val species: SpeciesUIModel,
    val height: Double?,
    val numberOfTrunks: Int?,
    val trunkGirth: Double?,
    val diameterOfCrown: Double,
    val heightOfTheFirstBranch: Double?,
    val conditionAssessment: Int?,
    val age: Int?,
    val treePlantingType: String?,
    val createTime: String,
    val updateTime: String,
    val authorId: Int?,
    val status: String?,
    val fileIds: List<Int>
) : Parcelable

@Parcelize
data class SpeciesUIModel(
    val id: String,
    val color: Int,
    val name: String
) : Parcelable
