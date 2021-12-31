package ru.ekbtrees.treemap.domain.interactors

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.utils.UploadResult

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

    suspend fun uploadFile(filePath: String) : Flow<UploadFileDto>

    suspend fun sendFile(image: Bitmap): UploadResult
}