package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.*


interface TreesRepository {

    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity>

    fun getTrees(): Collection<TreeEntity>

    fun getTreeBy(id: String): TreeEntity

    fun getAllSpecies(): Collection<SpeciesEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity)
}