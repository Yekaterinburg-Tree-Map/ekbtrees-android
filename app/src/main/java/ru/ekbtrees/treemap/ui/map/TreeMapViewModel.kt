package ru.ekbtrees.treemap.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import javax.inject.Inject

@HiltViewModel
class TreeMapViewModel @Inject constructor(
    private val interactor: TreesInteractor
) : ViewModel() {

    var cameraPosition: CameraPosition? = null
    lateinit var trees: Collection<TreeEntity>


    fun prepareData() {
        trees = interactor.getTrees()
    }
}