package ru.ekbtrees.treemap.ui.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.map.cluster.TreeClusterItem

class TreeMapFragment : Fragment() {

    private lateinit var treeMapViewModel: TreeMapViewModel
    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<TreeClusterItem>

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        setUpCamera()
        setUpCluster()
    }

    private fun setUpCamera() {
        val cityCenter = LatLng(56.835378, 60.611970)
        map.setMinZoomPreference(11f)
        if (treeMapViewModel.cameraPosition != null)
            map.moveCamera(CameraUpdateFactory.newCameraPosition(treeMapViewModel.cameraPosition))
        else // if you have location permission, move camera at user location
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCenter, 11f))
        val cityBounds = LatLngBounds(
            LatLng(56.777584, 60.492406), // SW bounds
            LatLng(56.901152, 60.675740) // NE bounds
        )
        map.setLatLngBoundsForCameraTarget(cityBounds)
    }

    private fun setUpCluster() {
        clusterManager = ClusterManager(this.context, map)

        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        addItems()
    }

    private fun addItems() {
        val trees = treeMapViewModel.trees
        if (trees != null) {
            for (tree in trees) {
                val offsetItem = TreeClusterItem(tree)
                clusterManager.addItem(offsetItem)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        treeMapViewModel = ViewModelProvider(this).get(TreeMapViewModel::class.java)
        if (treeMapViewModel.trees == null)
            treeMapViewModel.trees = treeMapViewModel.getTrees(requireContext())
        return inflater.inflate(R.layout.fragment_tree_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        treeMapViewModel.cameraPosition = map.cameraPosition
    }

    companion object {
        fun newInstance(): TreeMapFragment {
            return TreeMapFragment()
        }
    }
}