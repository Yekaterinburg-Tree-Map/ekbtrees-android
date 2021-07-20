package ru.ekbtrees.treemap.ui.map

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class TreeMapClusterManagerBuilder private constructor() {

    /**
     * Класс для кластеризации
     * */
    class TreeClusterItem(
        private val position: LatLng,
        private val title: String? = null,
        private val snippet: String? = null
    ) : ClusterItem {

        override fun getPosition(): LatLng = position

        override fun getTitle(): String? = title

        override fun getSnippet(): String? = snippet
    }

    /**
     * Класс отрисовки кластеров
     * */
    private class ClusterRenderer<T : ClusterItem>(
        context: Context,
        map: GoogleMap, clusterManager: ClusterManager<T>
    ) : DefaultClusterRenderer<T>(context, map, clusterManager) {

        override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
            return true // всегда выводится кластер
        }
    }

    companion object Builder {
        fun buildClusterManager(
            context: Context,
            googleMap: GoogleMap
        ): ClusterManager<TreeClusterItem> {
            val clusterManager = ClusterManager<TreeClusterItem>(context, googleMap)
            clusterManager.renderer =
                ClusterRenderer(context, googleMap, clusterManager)
            clusterManager.setAnimation(false)
            clusterManager.setOnClusterClickListener { clusterItem ->
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    clusterItem.position,
                    googleMap.cameraPosition.zoom + 2
                )
                googleMap.animateCamera(cameraUpdate)
                true
            }
            return clusterManager
        }
    }
}