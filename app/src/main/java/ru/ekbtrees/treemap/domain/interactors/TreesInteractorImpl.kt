package ru.ekbtrees.treemap.domain.interactors

import android.util.Log
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

class TreesInteractorImpl(private val treesRepository: TreesRepository) : TreesInteractor {

    override fun getTrees(): Collection<TreeEntity> {
        return treesRepository.getTreesInClusteringBy()
    }

    override fun getTreeSpecies(): Collection<SpeciesEntity> {
        return treesRepository.getAllSpecies()
    }

    override suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity) {
        Log.d(
            "TreesInteractor",
            "Got TreeDetail: id: ${treeDetail.id}, species: ${treeDetail.species.name}"
        )
    }
}