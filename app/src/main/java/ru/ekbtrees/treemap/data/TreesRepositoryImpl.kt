package ru.ekbtrees.treemap.data

import ru.ekbtrees.treemap.ClusterTreesEntity
import ru.ekbtrees.treemap.TreeEntity
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

class TreesRepositoryImpl: TreesRepository {

    override fun getAllClusteringTrees(): Collection<ClusterTreesEntity> {
        TODO("Not yet implemented")
    }

    override fun getTreesInClusteringBy(): Collection<TreeEntity> {
        TODO("Not yet implemented")
    }

    override fun getTreeBy(id: String): TreeEntity {
        TODO("Not yet implemented")
    }

}