package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    fun getTrees(): Collection<TreeEntity>

    fun getTreeDetailBy(id: String): TreeDetailEntity

    fun uploadTreeDetail(treeDetail: TreeDetailEntity)
}