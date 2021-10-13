package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.*

interface TreesRepository {

    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity>

    suspend fun getSpecies(): Collection<SpeciesEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): UploadResult

    suspend fun uploadNewTreeDetail(treeDetail: NewTreeDetailEntity): UploadResult
}

/** Класс для результата загрузки */
sealed class UploadResult {
    object Success: UploadResult()
    object Failure: UploadResult()
}