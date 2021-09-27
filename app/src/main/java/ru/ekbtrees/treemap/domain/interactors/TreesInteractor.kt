package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun createNewTree(newTreeDetailEntity: NewTreeDetailEntity): Boolean

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): Boolean

    suspend fun getAllSpecies(): Collection<SpeciesEntity>
}