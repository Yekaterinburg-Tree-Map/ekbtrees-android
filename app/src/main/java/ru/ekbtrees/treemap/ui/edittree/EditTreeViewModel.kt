package ru.ekbtrees.treemap.ui.edittree

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mappers.toNewTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toSpeciesUIModel
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.SpeciesUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel
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
) : BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeDetailEffect>() {

    private var treeId: String? = null

    suspend fun getTreeSpecies(): Collection<SpeciesEntity> {
        return interactor.getAllSpecies()
    }

    /**
     * Возвращает SpeciesEntity по его названию.
     * @return [SpeciesEntity] или null (если не удалось найти)
     * */
    suspend fun getSpeciesByName(speciesName: String): SpeciesUIModel? {
        val species = interactor.getAllSpecies()
        return species.find {
            it.name == speciesName
        }?.toSpeciesUIModel()
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
                treeId = instanceValue.treeId
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
                onNewTreeLocation(instanceValue.treeDetail)
            }
        }
    }

    override fun createInitialState(): EditTreeContract.EditTreeViewState {
        return EditTreeContract.EditTreeViewState.Idle
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is EditTreeContract.EditTreeEvent.OnReloadButtonClicked -> {
                reloadTreeData()
            }
            is EditTreeContract.EditTreeEvent.OnSaveButtonClicked -> {
                viewModelScope.launch {
                    when (event.treeDetail) {
                        is EditTreeContract.TreeDetailFragmentModel.NewTreeDetail -> {
                            interactor.createNewTree(event.treeDetail.newTreeDetail.toNewTreeDetailEntity())
                        }
                        is EditTreeContract.TreeDetailFragmentModel.TreeDetail -> {
                            val result =
                                interactor.uploadTreeDetail(event.treeDetail.treeDetail.toTreeDetailEntity())
                            if (result) {
                                setEffect {
                                    EditTreeContract.TreeDetailEffect.BackOnBackStack
                                }
                            } else {
                                setEffect {
                                    EditTreeContract.TreeDetailEffect.ShowErrorMessage
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun reloadTreeData() {
        viewModelScope.launch {
            if (treeId != null) {
                try {
                    val treeDetail = interactor.getTreeDetailBy(treeId!!)
                    setState(EditTreeContract.EditTreeViewState.DataLoaded(treeData = treeDetail.toTreeDetailUIModel()))
                } catch (e: Exception) {
                    setState(EditTreeContract.EditTreeViewState.Error)
                }
            }
        }
    }

    /**
     * Обрабатывет [treeDetail] и задаёт view state NewTreeData, если поле id пустое, иначе DataLoaded.
     * */
    private fun onNewTreeLocation(treeDetail: TreeDetailUIModel) {
        if (treeDetail.id == "") {
            val newTreeDetail = NewTreeDetailUIModel(
                coord = treeDetail.coord,
                species = treeDetail.species,
                height = treeDetail.height,
                numberOfTrunks = treeDetail.numberOfTrunks,
                trunkGirth = treeDetail.trunkGirth,
                diameterOfCrown = treeDetail.diameterOfCrown,
                heightOfTheFirstBranch = treeDetail.heightOfTheFirstBranch,
                conditionAssessment = treeDetail.conditionAssessment,
                age = treeDetail.age,
                treePlantingType = treeDetail.treePlantingType,
                createTime = treeDetail.createTime,
                updateTime = treeDetail.updateTime,
                authorId = treeDetail.authorId,
                status = treeDetail.status,
                fileIds = treeDetail.fileIds
            )
            setState(EditTreeContract.EditTreeViewState.NewTreeData(newTreeDetail))
        } else {
            setState(EditTreeContract.EditTreeViewState.DataLoaded(treeDetail))
        }
    }
}