package ru.ekbtrees.treemap.ui.edittree

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import javax.inject.Inject

@HiltViewModel
class EditTreeViewModel @Inject constructor(private val interactor: TreesInteractor) :
    BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeMapEffect>() {
    fun getTreeSpecies(): Array<SpeciesEntity> {
        return interactor.getTreeSpecies().toTypedArray()
    }

    override fun createInitialState(): EditTreeContract.EditTreeViewState {
        return EditTreeContract.EditTreeViewState.Idle
    }

    override fun handleEvent(event: UiEvent) {
        when(event) {
            is EditTreeContract.EditTreeEvent.OnReloadDataLaunched -> {
                // Launch loading tree data
            }
            is EditTreeContract.EditTreeEvent.OnSaveDataLaunched -> {
                // Save tree data
            }
        }
    }
}