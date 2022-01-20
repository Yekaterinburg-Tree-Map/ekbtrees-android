package ru.ekbtrees.treemap.ui.edittree

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.entity.NewTreeDetailEntity
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.domain.interactors.file.FilesInteractor
import ru.ekbtrees.treemap.domain.utils.Resource
import ru.ekbtrees.treemap.domain.utils.UploadResult
import ru.ekbtrees.treemap.ui.mappers.*
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeInputField
import javax.inject.Inject

private const val TAG = "EditTreeViewModel"

/**
 * ViewModel для [EditTreeFragment].
 * Для запуска нужно передать [EditTreeInstanceValue] в метод [provideInstanceValue].
 * */
@HiltViewModel
@Suppress("UNUSED_VARIABLE", "unused")
class EditTreeViewModel @Inject constructor(
    private val treesInteractor: TreesInteractor,
    private val filesInteractor: FilesInteractor
) : BaseViewModel<EditTreeContract.EditTreeEvent, EditTreeContract.EditTreeViewState, EditTreeContract.TreeDetailEffect>() {

    private var treeId: String? = null
    private var heightOfTheFirstBranch: String? = null
    private var numberOfTrunks: String? = null
    private var trunkGirth: String? = null
    private var diameterOfCrown: String? = null
    private var conditionAssessment: String? = null
    private var age: String? = null
    private var plantingType: String? = null
    private var species: String? = null
    private var status: String? = null
    private val fileIds = mutableListOf<Int>()

    suspend fun getTreeSpecies(): Collection<SpeciesEntity> {
        return treesInteractor.getAllSpecies()
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
                        val treeDetail = treesInteractor.getTreeDetailBy(instanceValue.treeId)
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
                uploadTreeDetail()
            }
            is EditTreeContract.EditTreeEvent.OnEditField -> {
                onEditField(event.newValue)
            }
            is EditTreeContract.EditTreeEvent.OnImagesSelected -> {
                uploadFiles(event.filePath)
            }
        }
    }

    private fun onEditField(value: EditTreeInputField) {
        when (value) {
            is EditTreeInputField.Age -> age = value.newValue
            is EditTreeInputField.ConditionAssessment -> conditionAssessment = value.newValue
            is EditTreeInputField.DiameterOfCrown -> diameterOfCrown = value.newValue
            is EditTreeInputField.HeightOfTheFirstBranch -> heightOfTheFirstBranch = value.newValue
            is EditTreeInputField.NumberOfTrunks -> numberOfTrunks = value.newValue
            is EditTreeInputField.PlantingType -> plantingType = value.newValue
            is EditTreeInputField.Species -> species = value.newValue
            is EditTreeInputField.Status -> status = value.newValue
            is EditTreeInputField.TrunkGirth -> trunkGirth = value.newValue
        }
    }

    private fun uploadFiles(filesPaths: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            filesPaths.forEach { filePath ->
                when (val resource = filesInteractor.sendFile(filePath)) {
                    is Resource.Error -> {
                        // что-то делаем. Скорее всего показываем ошибку отправки этого фото.
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "Successful upload file $filePath=${resource.data}")
                        // тоже что-то делаем. Мутируем текущее состояние.
                    }
                }
            }
        }
    }

    private fun uploadTreeDetail() {
        val handleUploadResult: (UploadResult) -> Unit = {
            when (it) {
                UploadResult.Failure -> setEffect {
                    return@setEffect EditTreeContract.TreeDetailEffect.ShowErrorMessage
                }
                UploadResult.Success -> setEffect {
                    return@setEffect EditTreeContract.TreeDetailEffect.BackOnBackStack
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            when (val state = currentState) {
                is EditTreeContract.EditTreeViewState.NewTreeData -> {
                    error("Пока не отладили, ничего не отправлять")
                    // TODO: 20.01.2022 Не забудь убрать, как убедишься в правильности работы
//                    val newTreeDetail = collectTreeDetail(state = state)
//                    viewModelScope.launch(Dispatchers.IO) {
//                        handleUploadResult(upload(treeDetail = newTreeDetail))
//                    }
                }
                is EditTreeContract.EditTreeViewState.DataLoaded -> {
                    error("Пока не отладили, ничего не отправлять")
                    // TODO: 20.01.2022 Не забудь убрать, как убедишься в правильности работы
//                    val treeDetail = collectTreeDetail(state = state)
//                    viewModelScope.launch(Dispatchers.IO) {
//                        handleUploadResult(upload(treeDetail = treeDetail))
//                    }
                }
                else -> {
                    error("Unexpected $state to upload tree detail.")
                }
            }
        }
    }

    private suspend fun upload(treeDetail: TreeDetailEntity): UploadResult {
        return treesInteractor.uploadTreeDetail(treeDetail)
    }

    private suspend fun upload(treeDetail: NewTreeDetailEntity): UploadResult {
        return treesInteractor.createNewTree(treeDetail)
    }

    private suspend fun collectTreeDetail(state: EditTreeContract.EditTreeViewState.NewTreeData): NewTreeDetailEntity {
        return NewTreeDetailEntity(
            coord = state.treeDetail.coord.toLatLonEntity(),
            species = getSpeciesByName(species!!),
            height = heightOfTheFirstBranch?.toDouble(),
            numberOfTrunks = numberOfTrunks?.toInt(),
            trunkGirth = trunkGirth?.toDouble(),
            diameterOfCrown = diameterOfCrown!!.toDouble(),
            heightOfTheFirstBranch = heightOfTheFirstBranch?.toDouble(),
            conditionAssessment = conditionAssessment?.toInt(),
            age = age?.toInt(),
            treePlantingType = plantingType,
            createTime = state.treeDetail.createTime,
            updateTime = System.currentTimeMillis().toString(),
            authorId = state.treeDetail.authorId,
            status = status,
            fileIds = fileIds
        )
    }

    private suspend fun collectTreeDetail(state: EditTreeContract.EditTreeViewState.DataLoaded): TreeDetailEntity {
        return TreeDetailEntity(
            id = state.treeData.id,
            coord = state.treeData.coord.toLatLonEntity(),
            species = getSpeciesByName(species!!),
            height = heightOfTheFirstBranch?.toDouble(),
            numberOfTrunks = numberOfTrunks?.toInt(),
            trunkGirth = trunkGirth?.toDouble(),
            diameterOfCrown = diameterOfCrown!!.toDouble(),
            heightOfTheFirstBranch = heightOfTheFirstBranch?.toDouble(),
            conditionAssessment = conditionAssessment?.toInt(),
            age = age?.toInt(),
            treePlantingType = plantingType,
            createTime = state.treeData.createTime,
            updateTime = System.currentTimeMillis().toString(),
            authorId = state.treeData.authorId,
            status = status,
            fileIds = emptyList()
        )
    }

    private fun reloadTreeData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (treeId != null) {
                try {
                    val treeDetail = treesInteractor.getTreeDetailBy(treeId!!)
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

    /**
     * Возвращает SpeciesEntity по его названию.
     * @return [SpeciesEntity] или null (если не удалось найти)
     * */
    private suspend fun getSpeciesByName(speciesName: String): SpeciesEntity {
        val species = treesInteractor.getAllSpecies()
        return species.find {
            it.name == speciesName
        }!!
    }
}