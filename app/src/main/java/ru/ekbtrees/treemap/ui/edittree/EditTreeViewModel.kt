package ru.ekbtrees.treemap.ui.edittree

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import javax.inject.Inject

@HiltViewModel
class EditTreeViewModel @Inject constructor(private val interactor: TreesInteractor) : ViewModel() {
    fun getTreeSpecies(): Array<SpeciesEntity> {
        return interactor.getTreeSpecies().toTypedArray()
    }
}