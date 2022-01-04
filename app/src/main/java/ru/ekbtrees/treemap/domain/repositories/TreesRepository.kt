package ru.ekbtrees.treemap.domain.repositories

import android.graphics.Bitmap
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.utils.UploadResult

interface TreesRepository {

    suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity>

    suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity>

    suspend fun getSpecies(): Collection<SpeciesEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): UploadResult

    suspend fun uploadNewTreeDetail(treeDetail: NewTreeDetailEntity): UploadResult

    suspend fun sendFile(image: Bitmap): UploadResult
}