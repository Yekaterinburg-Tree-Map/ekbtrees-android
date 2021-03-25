package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.ClusterTreesEntity
import ru.ekbtrees.treemap.TreeEntity


interface TreesRepository {

    fun getAllClusteringTrees(): Collection<ClusterTreesEntity>

    fun getTreesInClusteringBy(): Collection<TreeEntity>

    fun getTreeBy(id: String): TreeEntity

}