package ru.ekbtrees.treemap.ui.edittree

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
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

    suspend fun getTreeSpecies(): Collection<SpeciesEntity> {
        return interactor.getAllSpecies()
    }

    fun provideInstanceValue(instanceValue: EditTreeInstanceValue) {
        when (instanceValue) {
            is EditTreeInstanceValue.TreeLocation -> {
                val newTreeDetail = NewTreeDetailUIModel(
                    coord = instanceValue.treeLocation,
                    species = null,
                    height = 0.0,
                    numberOfTrunks = 0,
                    trunkGirth = 0.0,
                    diameterOfCrown = 0.0,
                    heightOfTheFirstBranch = 0.0,
                    conditionAssessment = 0,
                    age = 0,
                    treePlantingType = "",
                    createTime = System.currentTimeMillis().toString(),
                    updateTime = System.currentTimeMillis().toString(),
                    authorId = 0, // TODO: Добавить автора, когда будет готова авторизация.
                    status = "",
                    fileIds = emptyList()
                )
                setState(EditTreeContract.EditTreeViewState.NewTreeData(newTreeDetail))
            }
            is EditTreeInstanceValue.TreeId -> {
                viewModelScope.launch {
                    setState(EditTreeContract.EditTreeViewState.DataLoading)
                    try {
                        val treeDetail = interactor.getTreeDetailBy(instanceValue.treeId)
                        setState(EditTreeContract.EditTreeViewState.DataLoaded(treeDetail.toTreeDetailUIModel()))
                    } catch (e: Exception) {
                        setState(EditTreeContract.EditTreeViewState.Error)
                    }
                }
            }
            is EditTreeInstanceValue.NewTreeLocation -> {
                viewModelScope.launch {
                    if (instanceValue.treeDetail.id == "") {
                        val newTreeDetail = NewTreeDetailUIModel(
                            coord = instanceValue.treeDetail.coord,
                            species = instanceValue.treeDetail.species,
                            height = instanceValue.treeDetail.height,
                            numberOfTrunks = instanceValue.treeDetail.numberOfTrunks,
                            trunkGirth = instanceValue.treeDetail.trunkGirth,
                            diameterOfCrown = instanceValue.treeDetail.diameterOfCrown,
                            heightOfTheFirstBranch = instanceValue.treeDetail.heightOfTheFirstBranch,
                            conditionAssessment = instanceValue.treeDetail.conditionAssessment,
                            age = instanceValue.treeDetail.age,
                            treePlantingType = instanceValue.treeDetail.treePlantingType,
                            createTime = instanceValue.treeDetail.createTime,
                            updateTime = instanceValue.treeDetail.updateTime,
                            authorId = instanceValue.treeDetail.authorId,
                            status = instanceValue.treeDetail.status,
                            fileIds = instanceValue.treeDetail.fileIds
                        )
                        setState(EditTreeContract.EditTreeViewState.NewTreeData(newTreeDetail))
                    } else {
                        setState(EditTreeContract.EditTreeViewState.DataLoaded(instanceValue.treeDetail))
                    }
                }
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
                    when (event.treeDetail) {
                        is EditTreeContract.TreeDetailFragmentModel.NewTreeDetail -> {
                            // TODO: interactor -> createNewTree
                        }
                        is EditTreeContract.TreeDetailFragmentModel.TreeDetail -> {
                            interactor.uploadTreeDetail(event.treeDetail.treeDetail.toTreeDetailEntity())
                        }
                    }
                }
            }
        }
    }
}