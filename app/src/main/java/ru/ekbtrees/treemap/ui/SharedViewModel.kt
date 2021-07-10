package ru.ekbtrees.treemap.ui

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

class SharedViewModel: ViewModel() {
    private val _treeSelected = Channel<String>()
    val treeSelected = _treeSelected.receiveAsFlow()

    suspend fun onTreeSelected(treeId: String) {
        _treeSelected.send(treeId)
    }

    private val _addNewTree = Channel<LatLng>()
    val addNewTree = _addNewTree.receiveAsFlow()

    suspend fun addNewTree(location: LatLng) {
        _addNewTree.send(location)
    }

    private val _permissionResultReceiver = MutableSharedFlow<Pair<Int, Boolean>>()
    val permissionResultReceiver = _permissionResultReceiver.asSharedFlow()

    /**
     * Выслать результат запроса разрешений из активити к подписанным фрагментам.
     * */
    suspend fun sendPermissionResult(requestCode: Int, isGranted: Boolean) {
        _permissionResultReceiver.emit(requestCode to isGranted)
    }
}