package ru.ekbtrees.treemap.ui

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import ru.ekbtrees.treemap.domain.entity.TreeEntity

class SharedViewModel: ViewModel() {
    private val _treeSelected = Channel<TreeEntity>()
    val treeSelected = _treeSelected.receiveAsFlow()

    suspend fun onTreeSelected(treeId: TreeEntity) {
        _treeSelected.send(treeId)
    }

    private val _addNewTree = Channel<LatLng>()
    val addNewTree = _addNewTree.receiveAsFlow()

    suspend fun addNewTree(location: LatLng) {
        _addNewTree.send(location)
    }
}