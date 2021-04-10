package ru.ekbtrees.treemap.ui.map.cluster

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import ru.ekbtrees.treemap.domain.entity.TreeEntity

class TreeClusterItem(private val treeEntity: TreeEntity): ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(treeEntity.coord.lat.toDouble(), treeEntity.coord.lon.toDouble())
    }

    override fun getTitle(): String {
        return treeEntity.species.name
    }

    override fun getSnippet(): String? {
        return null
    }
}