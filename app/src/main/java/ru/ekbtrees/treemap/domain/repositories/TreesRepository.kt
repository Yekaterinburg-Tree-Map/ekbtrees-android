package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.*


interface TreesRepository {

    fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    fun getTrees(): Collection<TreeEntity>

    fun getTreeBy(id: String): TreeEntity

    fun getAllSpecies(): Collection<SpeciesEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity)
}