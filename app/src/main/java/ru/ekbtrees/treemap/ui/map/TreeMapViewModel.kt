package ru.ekbtrees.treemap.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import ru.ekbtrees.treemap.data.TreesRepositoryImpl
import ru.ekbtrees.treemap.domain.entity.TreeEntity

class TreeMapViewModel : ViewModel() {
    var cameraPosition: CameraPosition? = null
    var trees: Collection<TreeEntity>? = null

    fun getTrees(context: Context): Collection<TreeEntity> {
        return TreesRepositoryImpl().loadTreesFromJSONAsset(context)
    }
}