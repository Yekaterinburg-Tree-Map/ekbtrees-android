package ru.ekbtrees.treemap.ui.map

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.mapper.LatLonMapper
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.intent.TreeMapIntent
import ru.ekbtrees.treemap.ui.viewstates.MapViewState
import ru.ekbtrees.treemap.ui.viewstates.TreesViewState

class TreeMapFragment : Fragment() {
    interface TreeMapCallback {
        fun onTreeSelected(treeId: String)
        fun addNewTree(coordinate: LatLng)
    }

    private lateinit var treeMapViewModel: TreeMapViewModel

    // map state
    private lateinit var map: GoogleMap
    private lateinit var addTreeButton: FloatingActionButton

    // pick tree location state
    private lateinit var treeMarker: ImageView
    private lateinit var treeEditButton: MaterialButton
    private lateinit var cancelButton: Button

    // tree preview
    private lateinit var treePreview: CardView
    private lateinit var previewTreeSpeciesText: TextView
    private lateinit var previewCloseButton: ImageButton
    private lateinit var previewShowDescriptionButton: MaterialButton

    private lateinit var callback: TreeMapCallback

    private var selectedCircle: Circle? = null

    private fun disableSelectedCircle() {
        selectedCircle?.strokeColor = Color.BLACK
        selectedCircle?.strokeWidth = 1f
    }

    private fun enableSelectedCircle(treeCircle: Circle) {
        selectedCircle = treeCircle
        selectedCircle?.strokeColor = Color.RED
        selectedCircle?.strokeWidth = 5f
    }

    private fun addTrees(items: Array<TreeEntity>) {
        for (item in items) {
            val circle = CircleOptions().apply {
                center(LatLonMapper().map(item.coord))
                radius(item.diameter.toDouble() / 2.0)
                fillColor(item.species.color)
                clickable(true)
                strokeWidth(1f)
            }
            val added = map.addCircle(circle)
            added.tag = item.id
        }
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync { googleMap ->
            map = googleMap
            setUpCamera()
            map.setOnCircleClickListener { circle ->
                val color = circle.fillColor xor 0x00ffffff
                circle.fillColor = color
                Toast.makeText(
                    requireContext().applicationContext,
                    circle.tag as String,
                    Toast.LENGTH_SHORT
                ).show()
            }
            addTreeButton.setOnClickListener {
                lifecycleScope.launch {
                    disableSelectedCircle()
                    treeMapViewModel.intent.send(TreeMapIntent.NewTreeLocation)
                }
            }

            observeViewModel()

            lifecycleScope.launch {
                treeMapViewModel.intent.send(TreeMapIntent.FetchTrees)
            }
        }
    }

    private fun setUpCamera() {
        map.setMinZoomPreference(MIN_ZOOM_LEVEL)
        if (treeMapViewModel.cameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(treeMapViewModel.cameraPosition))
        } else {// if you have location permission, move camera at user location
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    EKATERINBURG_CENTER_POSITION,
                    MIN_ZOOM_LEVEL
                )
            )
        }
        map.setLatLngBoundsForCameraTarget(EKATERINBURG_CAMERA_BOUNDS)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tree_map, container, false)
        treeMarker = view.findViewById(R.id.tree_marker)
        treeEditButton = view.findViewById(R.id.edit_tree_button)
        cancelButton = view.findViewById(R.id.cancel_button)
        addTreeButton = view.findViewById(R.id.add_tree_button)
        treePreview = view.findViewById(R.id.tree_preview)
        previewCloseButton = view.findViewById(R.id.preview_close_button)
        previewTreeSpeciesText = view.findViewById(R.id.preview_tree_species_text)
        previewShowDescriptionButton = view.findViewById(R.id.preview_tree_description_button)

        treeMapViewModel =
            ViewModelProvider(
                this,
                TreeMapViewModelFactory(requireContext().applicationContext)
            ).get(TreeMapViewModel::class.java)

        previewCloseButton.setOnClickListener {
            disableSelectedCircle()
            treePreview.visibility = View.GONE
        }

        previewShowDescriptionButton.setOnClickListener {
            Toast.makeText(requireContext(), "Show tree description.", Toast.LENGTH_SHORT).show()
        }

        callback = context as TreeMapCallback

        return view
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            whenStarted {
                treeMapViewModel.mapState.collect { mapViewState ->
                    if (mapViewState is MapViewState.MapState) {
                        addTreeButton.show()
                    } else {
                        addTreeButton.hide()
                    }
                    if (mapViewState is MapViewState.MapPickTreeLocationState) {
                        treeMarker.visibility = View.VISIBLE
                        treeEditButton.visibility = View.VISIBLE
                        cancelButton.visibility = View.VISIBLE
                    } else {
                        treeMarker.visibility = View.GONE
                        treeEditButton.visibility = View.GONE
                        cancelButton.visibility = View.GONE
                    }
                    treeMarker.visibility =
                        if (mapViewState is MapViewState.MapPickTreeLocationState) View.VISIBLE
                        else View.GONE
                    treeEditButton.visibility =
                        if (mapViewState is MapViewState.MapPickTreeLocationState) View.VISIBLE
                        else View.GONE
                    cancelButton.visibility =
                        if (mapViewState is MapViewState.MapPickTreeLocationState) View.VISIBLE
                        else View.GONE

                    when (mapViewState) {
                        is MapViewState.Idle -> {
                        }
                        is MapViewState.MapState -> {
                            map.setOnCircleClickListener { treeCircle ->
                                disableSelectedCircle()

                                enableSelectedCircle(treeCircle)

                                val tag = treeCircle.tag as String
                                val treeEntity = treeMapViewModel.getTreeBy(id = tag)
                                previewTreeSpeciesText.text = treeEntity.species.name
                                treePreview.visibility = View.VISIBLE
                            }
                        }
                        is MapViewState.MapErrorState -> {
                            // show error text or picture
                        }
                        is MapViewState.MapPickTreeLocationState -> {
                            treePreview.visibility = View.GONE
                            map.setOnCircleClickListener { }
                            val cameraUpdateFactory =
                                CameraUpdateFactory.newLatLngZoom(map.cameraPosition.target, 18f)
                            map.animateCamera(cameraUpdateFactory)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            treeMapViewModel.treeDataState.collect { treeDataViewState ->
                when (treeDataViewState) {
                    is TreesViewState.Idle -> {
                    }
                    is TreesViewState.TreesLoadingState -> {
                        Log.d(TAG, "Got TreesLoadingState.")
                        // show progress bar over the map
                    }
                    is TreesViewState.TreesLoadedState -> {
                        addTrees(treeDataViewState.trees)
                    }
                    is TreesViewState.TreesLoadingErrorState -> {
                        Toast.makeText(
                            requireContext().applicationContext,
                            treeDataViewState.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMap()

        treeEditButton.setOnClickListener {
            callback.addNewTree(map.cameraPosition.target)
        }

        cancelButton.setOnClickListener {
            lifecycleScope.launch {
                treeMapViewModel.intent.send(TreeMapIntent.RequestMapState)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        treeMapViewModel.cameraPosition = map.cameraPosition
    }

    companion object {
        private const val TAG = "TreeMapFragment"
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