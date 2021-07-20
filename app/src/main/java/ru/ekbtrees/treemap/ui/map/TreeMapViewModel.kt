package ru.ekbtrees.treemap.ui.map

import com.google.android.gms.maps.model.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.ui.mappers.RegionBoundsUIModelMapper
import ru.ekbtrees.treemap.ui.model.RegionBoundsUIModel
import javax.inject.Inject
import ru.ekbtrees.treemap.ui.mvi.base.BaseViewModel
import ru.ekbtrees.treemap.ui.mvi.base.UiEvent
import ru.ekbtrees.treemap.ui.mvi.contract.TreeMapContract
import kotlin.Exception

private const val TAG = "TreeMapViewModel"

@HiltViewModel
class TreeMapViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<TreeMapContract.TreeMapEvent, TreeMapContract.MapViewState, TreeMapContract.TreeMapEffect>() {

    var cameraPosition: CameraPosition? = null

    private val _treeMapDataState = MutableStateFlow<TreeMapContract.DataState>(TreeMapContract.DataState.Idle)
    val treeDataState: StateFlow<TreeMapContract.DataState> = _treeMapDataState.asStateFlow()

    suspend fun getClusterTreesInRegion(regionBoundsUIModel: RegionBoundsUIModel) {
        _treeMapDataState.value = TreeMapContract.DataState.Loading
        try {
            val trees = interactor.getMapTreesInRegion(RegionBoundsUIModelMapper().map(regionBoundsUIModel))
            val data = TreeMapContract.LoadedData.TreeClusters(trees)
            _treeMapDataState.value = TreeMapContract.DataState.Loaded(data = data)
        } catch (e: Exception) {
            _treeMapDataState.value = TreeMapContract.DataState.Error
        }
    }

    suspend fun uploadTreesInRegion(regionBoundsUIModel: RegionBoundsUIModel) {
        _treeMapDataState.value = TreeMapContract.DataState.Loading
        try {
            val trees = interactor.getMapTreesInRegion(RegionBoundsUIModelMapper().map(regionBoundsUIModel))
            val data = TreeMapContract.LoadedData.Trees(trees = trees)
            _treeMapDataState.value = TreeMapContract.DataState.Loaded(data = data)
        } catch (e: Exception) {
            _treeMapDataState.value = TreeMapContract.DataState.Error
        }
    }

    fun getTreeBy(id: String): TreeEntity {
        if (treeDataState.value is TreeMapContract.DataState.Loaded) {
            val data = (treeDataState.value as TreeMapContract.DataState.Loaded).data
            val treeData: Collection<TreeEntity>
            if (data is TreeMapContract.LoadedData.Trees) {
                treeData = data.trees
            } else {
                throw IllegalAccessException()
            }
            treeData.forEach { treeEntity ->
                if (treeEntity.id == id)
                    return treeEntity
            }
        } else {
            throw Exception("Tree data wasn't loaded.")
        }
        throw Exception("Unknown tree id: $id")
    }

    override fun createInitialState(): TreeMapContract.MapViewState {
        return TreeMapContract.MapViewState.Idle
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is TreeMapContract.TreeMapEvent.OnMapViewReady -> {
                setState(TreeMapContract.MapViewState.MapState)
            }
            is TreeMapContract.TreeMapEvent.OnAddTreeButtonClicked -> {
                setState(TreeMapContract.MapViewState.MapPickTreeLocationState)
            }
            is TreeMapContract.TreeMapEvent.OnAddTreeCanceled -> {
                setState(TreeMapContract.MapViewState.MapState)
            }
        }
    }
}