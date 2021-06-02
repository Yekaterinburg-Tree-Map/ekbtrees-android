package ru.ekbtrees.treemap.ui.edittree

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint
import ru.ekbtrees.treemap.R

private const val TAG = "EditTreeFragment"
private const val LAT_PARAM = "latitude"
private const val LON_PARAM = "longitude"

@AndroidEntryPoint
class EditTreeFragment : Fragment() {
    private lateinit var treeLocation: LatLng

    private val viewModel: EditTreeViewModel by viewModels()

    private lateinit var map: GoogleMap
    private lateinit var latitudeValue: TextView
    private lateinit var longitudeValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val latitude = it.getDouble(LAT_PARAM)
            val longitude = it.getDouble(LON_PARAM)
            treeLocation = LatLng(latitude, longitude)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_tree, container, false)

        latitudeValue = view.findViewById(R.id.latitude_value)
        longitudeValue = view.findViewById(R.id.longitude_value)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync { googleMap ->
            map = googleMap
            val circleOptions = CircleOptions().apply {
                center(treeLocation)
                radius(1.5)
                fillColor(Color.GREEN)
                strokeColor(Color.BLACK)
                strokeWidth(2f)
            }
            map.addCircle(circleOptions)
            val zoomLevel = 19f
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLocation, zoomLevel))
            map.setMinZoomPreference(zoomLevel)
            map.setMaxZoomPreference(zoomLevel)
            val boundsAlpha = 0.000001f
            val bounds = LatLngBounds(
                LatLng(treeLocation.latitude - boundsAlpha, treeLocation.longitude - boundsAlpha),
                LatLng(treeLocation.latitude + boundsAlpha, treeLocation.longitude + boundsAlpha)
            )
            map.setLatLngBoundsForCameraTarget(bounds)
            val mapView = mapFragment.view
            mapView?.isClickable = false
        }
        latitudeValue.text = treeLocation.latitude.toString()
        longitudeValue.text = treeLocation.longitude.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance(treeLocation: LatLng) =
            EditTreeFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LAT_PARAM, treeLocation.latitude)
                    putDouble(LON_PARAM, treeLocation.longitude)
                }
            }
    }
}