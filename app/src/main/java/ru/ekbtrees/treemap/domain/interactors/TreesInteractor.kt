package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.TreeEntity

interface TreesInteractor {
    fun getTrees(): Collection<TreeEntity>
}