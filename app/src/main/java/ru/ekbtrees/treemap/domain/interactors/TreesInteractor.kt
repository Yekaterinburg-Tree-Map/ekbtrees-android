package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    fun getTrees(): Collection<TreeEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity)

    fun getTreeSpecies(): Collection<SpeciesEntity>
}