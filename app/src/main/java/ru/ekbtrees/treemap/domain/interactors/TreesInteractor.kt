package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    fun getTrees(): Collection<TreeEntity>

    fun getTreeSpecies(): Collection<SpeciesEntity>

    suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity)
}