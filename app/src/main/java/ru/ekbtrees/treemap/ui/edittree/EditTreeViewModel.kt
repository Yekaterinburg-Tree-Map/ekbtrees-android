package ru.ekbtrees.treemap.ui.edittree

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.constants.NetworkConstants
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.domain.utils.UploadResult
import ru.ekbtrees.treemap.ui.mappers.toNewTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toSpeciesUIModel
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.toTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.PhotoUIModel
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
    private val interactor: TreesInteractor
) : BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeDetailEffect>() {

    private var treeId: String? = null

    suspend fun getTreeSpecies(): Collection<SpeciesEntity> {
        return interactor.getAllSpecies()
    }

    fun uploadPhotos(photos: List<PhotoUIModel>): Flow<PhotoUIModel> = flow {
        photos.forEach { photo ->
            val result = interactor.uploadFile(photo.uri.toString())
            result.collect {
                when (it) {
                    is UploadFileDto.Success -> {
                        Log.d(TAG, "uploadPhotos: ${it.fileId}")
                        emit(photo.copy(link = "${NetworkConstants.FILE_DOWNLOAD_URL}${it.fileId}"))
                    }
                    is UploadFileDto.Error -> {
                        Log.d(
                            TAG,
                            "uploadPhotos: error ${photo.uri}. ${it.exception.printStackTrace()}"
                        )
                    }
                }
            }
        }
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
                uploadTreeDetail(event.treeDetail)
            }
            is EditTreeContract.EditTreeEvent.OnImagesSelected -> {
                uploadFiles(event.image)
            }
        }
    }

    private fun uploadFiles(
        //treeDetail: EditTreeContract.TreeDetailFragmentModel,
        filesPaths: List<Bitmap>
    ) {
        viewModelScope.launch {
            filesPaths.forEach { filePath ->
                when (interactor.sendFile(filePath)) {
                    UploadResult.Success -> {
                    }
                    UploadResult.Failure -> {
                    }
                }
//                interactor.uploadFile(filePath).collect { uploadFile ->
//                    when (uploadFile) {
//                        is UploadFileDto.Progress -> {
//                        }
//                        is UploadFileDto.Success -> {
//                            Log.d("file_upload", "successful: ${uploadFile.fileId}")
//                        }
//                        is UploadFileDto.Error -> {
//                            Log.e("file_upload", "FAILED")
//                            uploadFile.throwable.printStackTrace()
//                        }
//                    }
//                }
            }
        }
    }

    private fun uploadTreeDetail(treeDetail: EditTreeContract.TreeDetailFragmentModel) {
        viewModelScope.launch {
            when (treeDetail) {
                is EditTreeContract.TreeDetailFragmentModel.NewTreeDetail -> {
                    val result =
                        interactor.createNewTree(treeDetail.newTreeDetail.toNewTreeDetailEntity())
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
                        interactor.uploadTreeDetail(treeDetail.treeDetail.toTreeDetailEntity())
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