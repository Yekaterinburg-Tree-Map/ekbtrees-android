package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity>

    fun getTrees(): Collection<TreeEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun createNewTree(newTreeDetailEntity: NewTreeDetailEntity)

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity)

    suspend fun getAllSpecies(): Collection<SpeciesEntity>

    fun getTreeSpecies(): Collection<SpeciesEntity>
}