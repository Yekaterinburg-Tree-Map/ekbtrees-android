package ru.ekbtrees.treemap.data

import android.content.Context
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

class TreesInteractorImpl(context: Context): TreesInteractor {

    private var repository: TreesRepository = TreesRepositoryImpl(context = context)

    override fun getTrees(): Collection<TreeEntity> {
        return repository.getTreesInClusteringBy()
    }
}