package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

class TreesInteractorImpl(private val treesRepository: TreesRepository) : TreesInteractor {

    override fun getTrees(): Collection<TreeEntity> {
        return treesRepository.getTreesInClusteringBy()
    }

    override suspend fun getTreeDetailBy(id: String): TreeDetailEntity {
        return treesRepository.getTreeDetailBy(id)
    }

    override suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity) {
        return treesRepository.uploadTreeDetail(treeDetail)
    }
}