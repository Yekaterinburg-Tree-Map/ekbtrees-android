package ru.ekbtrees.treemap.ui.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.data.TreesInteractorImpl
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.intent.TreeMapIntent
import ru.ekbtrees.treemap.ui.viewstates.MapViewState
import ru.ekbtrees.treemap.ui.viewstates.TreesViewState
import kotlin.Exception

private const val TAG = "TreeMapViewModel"

class TreeMapViewModel(context: Context) : ViewModel() {

    var cameraPosition: CameraPosition? = null
    private lateinit var trees: Array<TreeEntity>

    private val interactor = TreesInteractorImpl(context = context)

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
                    is TreeMapIntent.RequestMapState -> {
                        Log.d(TAG, "Got RequestMap intent. Send MapLoadedState")
                        _mapState.value = MapViewState.MapState
                    }
                    is TreeMapIntent.NewTreeLocation -> {
                        Log.d(TAG, "Got NewTreeLocation intent. Send MapAddNewTreeState")
                        _mapState.value = MapViewState.MapPickTreeLocationState
                    }
                    is TreeMapIntent.FetchTrees -> {
                        Log.d(TAG, "Got FetchTrees intent.")
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