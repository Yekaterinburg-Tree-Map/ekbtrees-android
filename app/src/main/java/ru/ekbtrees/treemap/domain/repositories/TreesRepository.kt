package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity


interface TreesRepository {

    fun getAllClusteringTrees(): Collection<ClusterTreesEntity>

    fun getTreesInClusteringBy(): Collection<TreeEntity>

    fun getTreeBy(id: String): TreeEntity

}