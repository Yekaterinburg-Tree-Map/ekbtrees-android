package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity


interface TreesRepository {

    fun getAllClusteringTrees(): Collection<ClusterTreesEntity>

    fun getTreesInClusteringBy(): Collection<TreeEntity>

    fun getTreeBy(id: String): TreeEntity

    fun getAllSpecies(): Collection<SpeciesEntity>

    suspend fun getTreeDetailBy(id: String): TreeDetailEntity

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity)
}