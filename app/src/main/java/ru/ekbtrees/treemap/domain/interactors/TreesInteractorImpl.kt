package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

class TreesInteractorImpl(private val treesRepository: TreesRepository) : TreesInteractor {

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

    override suspend fun createNewTree(newTreeDetailEntity: NewTreeDetailEntity): Boolean {
        return treesRepository.uploadNewTreeDetail(newTreeDetailEntity)
    }

    override suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): Boolean {
        return treesRepository.uploadTreeDetail(treeDetail)
    }

    override suspend fun getAllSpecies(): Collection<SpeciesEntity> {
        return treesRepository.getSpecies()
    }
}