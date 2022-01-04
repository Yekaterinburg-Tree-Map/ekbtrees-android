package ru.ekbtrees.treemap.domain.interactors

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.FilesRepository
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import ru.ekbtrees.treemap.domain.utils.UploadResult

class TreesInteractorImpl(
    private val treesRepository: TreesRepository,
    private val filesRepository: FilesRepository
) : TreesInteractor {

    override suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity> {
        return try {
            treesRepository.getTreeClusters(regionBoundsEntity = regionBoundsEntity)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity> {
        return try {
            treesRepository.getMapTreesInRegion(regionBoundsEntity)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTreeDetailBy(id: String): TreeDetailEntity {
        return treesRepository.getTreeDetailBy(id)
    }

    override suspend fun createNewTree(newTreeDetailEntity: NewTreeDetailEntity): UploadResult {
        return treesRepository.uploadNewTreeDetail(newTreeDetailEntity)
    }

    override suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): UploadResult {
        return treesRepository.uploadTreeDetail(treeDetail)
    }

    override suspend fun getAllSpecies(): Collection<SpeciesEntity> {
        return treesRepository.getSpecies()
    }

    override suspend fun uploadFile(filePath: String): Flow<UploadFileDto> {
        return filesRepository.upload(filePath)
    }

    override suspend fun sendFile(image: Bitmap): UploadResult {
        return treesRepository.sendFile(image)
    }
}