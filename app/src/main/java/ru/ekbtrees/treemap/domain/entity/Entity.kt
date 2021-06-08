package ru.ekbtrees.treemap.domain.entity

data class ClusterTreesEntity(val count: Int, val coord: LatLonEntity)

data class TreeEntity(val id: String, val diameter: Float, val species: SpeciesEntity, val coord: LatLonEntity)

data class SpeciesEntity(val id: String, val color: Int, val name: String)

data class LatLonEntity(val lat: Double, val lon: Double)

