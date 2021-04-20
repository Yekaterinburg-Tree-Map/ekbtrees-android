package ru.ekbtrees.treemap.ui.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.domain.entity.TreeEntity

class TreeMapFragment : Fragment() {
    private lateinit var treeMapViewModel: TreeMapViewModel
    private lateinit var map: GoogleMap
    private lateinit var marker: Marker
    private lateinit var treeMarker: ImageView
    private lateinit var addTreeButton: Button

    private fun addTrees(items: Collection<TreeEntity>) {
        for (item in items) {
            val circle = CircleOptions().apply {
                center(item.coord.asLatLng())
                radius(item.diameter.toDouble() / 2.0)
                fillColor(item.species.color)
                clickable(true)
                strokeWidth(1f)
            }
            val added = map.addCircle(circle)
            added.tag = item.id
        }
    }

    private fun setUpCamera() {
        map.setMinZoomPreference(MIN_ZOOM_LEVEL)
        if (treeMapViewModel.cameraPosition != null)
            map.moveCamera(CameraUpdateFactory.newCameraPosition(treeMapViewModel.cameraPosition))
        else // if you have location permission, move camera at user location
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    EKATERINBURG_CENTER_POSITION,
                    MIN_ZOOM_LEVEL
                )
            )
        map.setLatLngBoundsForCameraTarget(EKATERINBURG_CAMERA_BOUNDS)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tree_map, container, false)
        treeMarker = view.findViewById(R.id.tree_marker)
        addTreeButton = view.findViewById(R.id.add_tree_button)

        treeMapViewModel =
            ViewModelProvider(
                this,
                TreeMapViewModelFactory(requireContext().applicationContext)
            ).get(TreeMapViewModel::class.java)
        treeMapViewModel.prepareData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync { googleMap ->
            map = googleMap
            setUpCamera()
            addTrees(treeMapViewModel.trees)
            map.setOnCameraIdleListener {
                GoogleMap.OnCameraIdleListener {
                    marker.position = map.cameraPosition.target
                }
            }
            map.setOnCircleClickListener { circle ->
                val color = circle.fillColor xor 0x00ffffff
                circle.fillColor = color
                Toast.makeText(
                    requireContext().applicationContext,
                    circle.tag as String,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        addTreeButton.setOnClickListener {
            val circleOpt = CircleOptions().apply {
                center(map.cameraPosition.target)
                radius(5.0)
                fillColor(R.color.black)
                strokeWidth(1f)
                strokeColor(R.color.white)
            }
            map.addCircle(circleOpt)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        treeMapViewModel.cameraPosition = map.cameraPosition
    }

    companion object {
        private val EKATERINBURG_CENTER_POSITION = LatLng(56.835378, 60.611970)
        private val EKATERINBURG_CAMERA_BOUNDS = LatLngBounds(
            LatLng(56.777584, 60.492406), // SW bounds
            LatLng(56.901152, 60.675740) // NE bounds
        )
        private const val MIN_ZOOM_LEVEL = 11f

        fun newInstance(): TreeMapFragment {
            return TreeMapFragment()
        }
    }
}