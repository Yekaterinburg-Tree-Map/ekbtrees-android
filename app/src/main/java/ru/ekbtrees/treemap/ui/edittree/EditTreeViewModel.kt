package ru.ekbtrees.treemap.ui.edittree

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import javax.inject.Inject

/**
 * ViewModel для [EditTreeFragment].
 * Для работы требуется добавить в аргумент фрагмента с ключём [EditTreeViewModel.INSTANCE_VALUE_KEY] и значением [EditTreeInstanceValue].
 * */
@HiltViewModel
class EditTreeViewModel @Inject constructor(
    private val interactor: TreesInteractor,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeMapEffect>() {

    init {
        handleViewInstanceValue(
            savedStateHandle.get<EditTreeInstanceValue>(INSTANCE_VALUE_KEY)
                ?: throw Exception("Instance value must be added.")
        )
    }

    fun getTreeSpecies(): Array<SpeciesEntity> {
        return interactor.getTreeSpecies().toTypedArray()
    }

    private fun handleViewInstanceValue(instanceValue: EditTreeInstanceValue) {
        when (instanceValue) {
            is EditTreeInstanceValue.TreeLocation -> {
                setState(EditTreeContract.EditTreeViewState.EmptyTreeDataState(instanceValue.treeLocation))
            }
            is EditTreeInstanceValue.TreeId -> {
                /**
                 *  На этом этапе делаем запрос к интерактору для получения подробной инфррмации о дереве
                 */
            }
        }
    }

    override fun createInitialState(): EditTreeContract.EditTreeViewState {
        return EditTreeContract.EditTreeViewState.Idle
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is EditTreeContract.EditTreeEvent.OnReloadButtonClicked -> {
                // Launch loading tree data by treeId
            }
            is EditTreeContract.EditTreeEvent.OnSaveButtonClicked -> {
                // Save tree data
            }
        }
    }

    companion object {
        const val INSTANCE_VALUE_KEY = "InstanceValue"
    }
}