package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.UploadResult

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun createNewTree(newTreeDetailEntity: NewTreeDetailEntity): UploadResult

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): UploadResult

    suspend fun getAllSpecies(): Collection<SpeciesEntity>
}