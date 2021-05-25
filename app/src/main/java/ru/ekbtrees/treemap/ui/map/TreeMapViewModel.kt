package ru.ekbtrees.treemap.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import javax.inject.Inject
import ru.ekbtrees.treemap.ui.intent.TreeMapIntent
import ru.ekbtrees.treemap.ui.viewstates.MapViewState
import ru.ekbtrees.treemap.ui.viewstates.TreesViewState
import kotlin.Exception

private const val TAG = "TreeMapViewModel"

@HiltViewModel
class TreeMapViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : ViewModel() {

    var cameraPosition: CameraPosition? = null
    private lateinit var trees: Array<TreeEntity>

    val intent = Channel<TreeMapIntent>(Channel.UNLIMITED)

    private val _mapState = MutableStateFlow<MapViewState>(MapViewState.MapState)
    val mapState: StateFlow<MapViewState> = _mapState.asStateFlow()

    private val _treeDataState = MutableStateFlow<TreesViewState>(TreesViewState.Idle)
    val treeDataState: StateFlow<TreesViewState> = _treeDataState.asStateFlow()

    init {
        handleIntent()
    }

    fun getTreeBy(id: String): TreeEntity {
        trees.forEach { treeEntity ->
            if (treeEntity.id == id)
                return treeEntity
        }
        throw Exception("Unknown tree id: $id")
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect { treeMapIntent ->
                when (treeMapIntent) {
                    is TreeMapIntent.OnAddTreeCanceled -> {
                        _mapState.value = MapViewState.MapState
                    }
                    is TreeMapIntent.OnAddTreeSelected -> {
                        _mapState.value = MapViewState.MapPickTreeLocationState
                    }
                    is TreeMapIntent.OnMapViewReady -> {
                        fetchTrees()
                    }
                }
            }
        }
    }

    private fun fetchTrees() {
        _treeDataState.value = TreesViewState.TreesLoadingState
        _treeDataState.value = try {
            trees = interactor.getTrees().toTypedArray()
            TreesViewState.TreesLoadedState(trees)
        } catch (e: Exception) {
            trees = emptyArray()
            TreesViewState.TreesLoadingErrorState("Failed to load trees.")
        }
    }
}