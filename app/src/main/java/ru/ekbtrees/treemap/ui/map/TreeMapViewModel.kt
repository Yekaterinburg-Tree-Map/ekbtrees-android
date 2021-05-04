package ru.ekbtrees.treemap.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import ru.ekbtrees.treemap.data.TreesInteractorImpl
import ru.ekbtrees.treemap.domain.entity.TreeEntity

class TreeMapViewModel(context: Context) : ViewModel() {

    var cameraPosition: CameraPosition? = null
    lateinit var trees: Collection<TreeEntity>

    private val interactor = TreesInteractorImpl(context = context)

    fun prepareData() {
        trees = interactor.getTrees()
    }
}