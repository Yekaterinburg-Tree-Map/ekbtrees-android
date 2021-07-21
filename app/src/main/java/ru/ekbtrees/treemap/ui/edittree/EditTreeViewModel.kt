package ru.ekbtrees.treemap.ui.edittree

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import javax.inject.Inject

/**
 * ViewModel для [EditTreeFragment].
 * Для запуска нужно передать [EditTreeInstanceValue] в метод [provideInstanceValue].
 * */
@HiltViewModel
class EditTreeViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeMapEffect>() {

    fun getTreeSpecies(): Array<SpeciesEntity> {
        return interactor.getTreeSpecies().toTypedArray()
    }

    fun provideInstanceValue(instanceValue: EditTreeInstanceValue) {
        when (instanceValue) {
            is EditTreeInstanceValue.TreeLocation -> {
                setState(EditTreeContract.EditTreeViewState.EmptyData(instanceValue.treeLocation))
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
                viewModelScope.launch {
                    interactor.uploadTreeDetail(event.treeDetail)
                }
            }
        }
    }
}