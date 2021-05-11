package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.TreeEntity

/**
 * Класс для бизнес логики связанной с деревьями
 */
interface TreesInteractor {
    fun getTrees(): Collection<TreeEntity>
}