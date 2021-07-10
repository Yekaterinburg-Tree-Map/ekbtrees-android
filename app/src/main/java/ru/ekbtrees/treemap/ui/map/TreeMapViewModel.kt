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
import ru.ekbtrees.treemap.ui.viewstates.TreesViewState
import kotlin.Exception

private const val TAG = "TreeMapViewModel"

@HiltViewModel
class TreeMapViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : BaseViewModel<TreeMapContract.TreeMapEvent, TreeMapContract.MapViewState, TreeMapContract.TreeMapEffect>() {

    var cameraPosition: CameraPosition? = null

    private val _treeDataState = MutableStateFlow<TreesViewState>(TreesViewState.Idle)
    val treeDataState: StateFlow<TreesViewState> = _treeDataState.asStateFlow()

    suspend fun getTreesInRegion(regionBoundsUIModel: RegionBoundsUIModel) {
        interactor.getMapTreesInRegion(RegionBoundsUIModelMapper().map(regionBoundsUIModel))
    }

    fun getTreeBy(id: String): TreeEntity {
        if (treeDataState.value is TreesViewState.TreesLoadedState) {
            val treeData = (treeDataState.value as TreesViewState.TreesLoadedState).trees
            treeData.forEach { treeEntity ->
                if (treeEntity.id == id)
                    return treeEntity
            }
        } else {
            throw Exception("Tree data wasn't loaded.")
        }
        throw Exception("Unknown tree id: $id")
    }

    private fun fetchTrees() {
        _treeDataState.value = TreesViewState.TreesLoadingState
        _treeDataState.value = try {
            val trees = interactor.getTrees().toTypedArray()
            TreesViewState.TreesLoadedState(trees)
        } catch (e: Exception) {
            TreesViewState.TreesLoadingErrorState("Failed to load trees.")
        }
    }

    override fun createInitialState(): TreeMapContract.MapViewState {
        return TreeMapContract.MapViewState.Idle
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is TreeMapContract.TreeMapEvent.OnMapViewReady -> {
                setState(TreeMapContract.MapViewState.MapState)
                fetchTrees()
            }
            is TreeMapContract.TreeMapEvent.OnAddTreeLaunched -> {
                setState(TreeMapContract.MapViewState.MapPickTreeLocationState)
            }
            is TreeMapContract.TreeMapEvent.OnAddTreeCanceled -> {
                setState(TreeMapContract.MapViewState.MapState)
            }
        }
    }
}