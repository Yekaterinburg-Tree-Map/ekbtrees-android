package ru.ekbtrees.treemap.ui.edittree

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.constants.NetworkConstants
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.domain.interactors.file.FilesInteractor
import ru.ekbtrees.treemap.domain.utils.Resource
import ru.ekbtrees.treemap.domain.utils.UploadResult
import ru.ekbtrees.treemap.ui.mappers.toNewTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toSpeciesUIModel
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.PhotoUiModel
import ru.ekbtrees.treemap.ui.model.SpeciesUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import javax.inject.Inject

private const val TAG = "EditTreeViewModel"

/**
 * ViewModel для [EditTreeFragment].
 * Для запуска нужно передать [EditTreeInstanceValue] в метод [provideInstanceValue].
 * */
@HiltViewModel
class EditTreeViewModel @Inject constructor(
    private val treesInteractor: TreesInteractor,
    private val filesInteractor: FilesInteractor
) : BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeDetailEffect>() {

    private var treeId: String? = null
    private val photoStateFlow = MutableStateFlow<List<PhotoUiModel>>(emptyList())

    suspend fun getTreeSpecies(): Collection<SpeciesEntity> {
        return treesInteractor.getAllSpecies()
    }

    /**
     * Возвращает SpeciesEntity по его названию.
     * @return [SpeciesEntity] или null (если не удалось найти)
     * */
    suspend fun getSpeciesByName(speciesName: String): SpeciesUIModel? {
        val species = treesInteractor.getAllSpecies()
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
                setState(
                    EditTreeContract.EditTreeViewState.NewTreeData(
                        newTreeDetail,
                        photoStateFlow
                    )
                )
            }
            is EditTreeInstanceValue.TreeId -> {
                treeId = instanceValue.treeId
                viewModelScope.launch {
                    setState(EditTreeContract.EditTreeViewState.DataLoading)
                    try {
                        val treeDetail = treesInteractor.getTreeDetailBy(instanceValue.treeId)
                        setState(
                            EditTreeContract.EditTreeViewState.DataLoaded(
                                treeDetail.toTreeDetailUIModel(),
                                photoStateFlow
                            )
                        )
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
                uploadTreeDetail(event.treeDetail)
            }
            is EditTreeContract.EditTreeEvent.OnImagesSelected -> {
                uploadFiles(event.filePath)
            }
        }
    }

    private fun uploadFiles(filesPaths: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (currentState) {
                is EditTreeContract.EditTreeViewState.DataLoaded -> {
                    error("Реализовать прикрепление фотографий к дереву")
                }
                is EditTreeContract.EditTreeViewState.NewTreeData -> {
                    filesPaths.forEachIndexed { index, filePath ->
                        val existedPhotos = photoStateFlow.value
                        val newPhotos: MutableList<PhotoUiModel> =
                            filesPaths.map { PhotoUiModel.Uploading(filePath) }.toMutableList()
                        photoStateFlow.value = newPhotos + existedPhotos
                        when (val resource = filesInteractor.sendFile(filePath)) {
                            is Resource.Success -> {
                                Log.d(TAG, "Successful upload file $filePath=${resource.data}")
                                newPhotos[index] =
                                    PhotoUiModel.Photo(NetworkConstants.FILE_DOWNLOAD_URL + resource.data.toString())
                                photoStateFlow.value = newPhotos + existedPhotos
                            }
                            is Resource.Error -> {
                                newPhotos[index] = PhotoUiModel.Error
                                photoStateFlow.value = newPhotos + existedPhotos
                            }
                        }
                    }
                }
                else -> {
                    error("Unexpected event for this state: $currentState")
                }
            }
        }
    }

    private fun uploadTreeDetail(treeDetail: EditTreeContract.TreeDetailFragmentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            when (treeDetail) {
                is EditTreeContract.TreeDetailFragmentModel.NewTreeDetail -> {
                    val result =
                        treesInteractor.createNewTree(treeDetail.newTreeDetail.toNewTreeDetailEntity())
                    setEffect {
                        return@setEffect if (result is UploadResult.Success) {
                            EditTreeContract.TreeDetailEffect.BackOnBackStack
                        } else {
                            EditTreeContract.TreeDetailEffect.ShowErrorMessage
                        }
                    }
                }
                is EditTreeContract.TreeDetailFragmentModel.TreeDetail -> {
                    val result =
                        treesInteractor.uploadTreeDetail(treeDetail.treeDetail.toTreeDetailEntity())
                    setEffect {
                        return@setEffect if (result is UploadResult.Success) {
                            EditTreeContract.TreeDetailEffect.BackOnBackStack
                        } else {
                            EditTreeContract.TreeDetailEffect.ShowErrorMessage
                        }
                    }
                }
            }
        }
    }

    private fun reloadTreeData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (treeId != null) {
                try {
                    val treeDetail = treesInteractor.getTreeDetailBy(treeId!!)
                    setState(
                        EditTreeContract.EditTreeViewState.DataLoaded(
                            treeData = treeDetail.toTreeDetailUIModel(),
                            photoStateFlow
                        )
                    )
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
            setState(
                EditTreeContract.EditTreeViewState.NewTreeData(
                    newTreeDetail,
                    photoStateFlow
                )
            )
        } else {
            setState(EditTreeContract.EditTreeViewState.DataLoaded(treeDetail, photoStateFlow))
        }
    }
}