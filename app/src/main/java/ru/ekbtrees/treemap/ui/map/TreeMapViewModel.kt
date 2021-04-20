package ru.ekbtrees.treemap.ui.map

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import ru.ekbtrees.treemap.data.TreesRepositoryImpl
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity

class TreeMapViewModel(context: Context) : ViewModel() {

    private val repository = TreesRepositoryImpl(context)
    var cameraPosition: CameraPosition? = null
    lateinit var trees: Collection<TreeEntity>
    val species = repository.getSpecies()

    fun prepareData() {
        trees = repository.getTreesInClusteringBy()
    }
}