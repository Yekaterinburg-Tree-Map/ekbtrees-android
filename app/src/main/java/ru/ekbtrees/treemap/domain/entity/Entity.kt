package ru.ekbtrees.treemap.domain.entity

data class ClusterTreesEntity(val count: Int, val coord: LatLonEntity)

data class TreeDetailEntity(val id: String)

data class TreeEntity(
    val id: String,
    val diameter: Float,
    val species: SpeciesEntity,
    val coord: LatLonEntity
)

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

data class SpeciesEntity(val id: String, val color: Int, val name: String)

data class LatLonEntity(val lat: Double, val lon: Double)

