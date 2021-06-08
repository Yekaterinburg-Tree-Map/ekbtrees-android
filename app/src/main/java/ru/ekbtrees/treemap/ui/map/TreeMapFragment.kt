package ru.ekbtrees.treemap.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.mappers.LatLonMapper
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.SharedViewModel
import ru.ekbtrees.treemap.ui.mvi.contract.TreeMapContract
import ru.ekbtrees.treemap.ui.viewstates.TreesViewState

@AndroidEntryPoint
class TreeMapFragment : Fragment() {

    // map state
    private lateinit var map: GoogleMap
    private lateinit var addTreeButton: FloatingActionButton

    // pick tree location state
    private lateinit var treeMarker: ImageView
    private lateinit var treeEditButton: FloatingActionButton
    private lateinit var cancelButton: FloatingActionButton

    // tree preview
    private lateinit var previewTree: CardView
    private lateinit var previewTreeSpeciesText: TextView
    private lateinit var previewTreePosition: TextView
    private lateinit var previewTreeDiameter: TextView
    private lateinit var previewCloseButton: ImageButton
    private lateinit var previewShowDescriptionButton: MaterialButton

    //tree description
    private lateinit var treeDescription: LinearLayout
    private lateinit var treeDescriptionImage: ImageView
    private lateinit var treeDescriptionSpecies: TextView
    private lateinit var treeDescriptionPosition: TextView
    private lateinit var treeDescriptionDiameter: TextView
    private lateinit var treeDescriptionCloseButton: ImageView

    private val treeMapViewModel: TreeMapViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            setUpCamera()

            observeViewModel()

            treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnMapViewReady)
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
        previewTree = view.findViewById(R.id.preview_tree)
        previewCloseButton = view.findViewById(R.id.preview_close_button)
        previewTreeSpeciesText = view.findViewById(R.id.preview_tree_species_text)
        previewTreePosition = view.findViewById(R.id.preview_tree_position)
        previewTreeDiameter = view.findViewById(R.id.preview_tree_diameter)

        previewShowDescriptionButton = view.findViewById(R.id.preview_tree_description_button)
        val treeDescriptionContainer = view.findViewById<LinearLayout>(R.id.description_tree_container)
        treeDescription = inflater.inflate(R.layout.fragment_tree_description, treeDescriptionContainer as ViewGroup, true) as LinearLayout
        treeDescription.visibility = View.GONE
        treeDescriptionCloseButton = treeDescription.findViewById(R.id.description_tree_close_button)
        treeDescriptionSpecies = treeDescription.findViewById(R.id.description_tree_species_text)
        treeDescriptionPosition = treeDescription.findViewById(R.id.description_tree_position)
        treeDescriptionDiameter = treeDescription.findViewById(R.id.description_tree_diameter_crown)

        addTreeButton.setOnClickListener {
            lifecycleScope.launch {
                disableSelectedCircle()
                treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeLaunched)
            }
        }

        previewCloseButton.setOnClickListener {
            disableSelectedCircle()
            previewTree.visibility = View.GONE
        }

        previewShowDescriptionButton.setOnClickListener {
            previewTree.visibility = View.GONE
            treeDescription.visibility = View.VISIBLE
        }
        treeDescriptionCloseButton.setOnClickListener {
            disableSelectedCircle()
            treeDescription.visibility = View.GONE
        }

        return view
    }

    /**
     * Выводит на экран View объекты состояния добавления дерева
     * */
    private fun showViews() {
        treeMarker.visibility = View.VISIBLE
        treeEditButton.show()
        cancelButton.show()
    }

    /**
     * Скрывает View объекты состояния добавления дерева
     * */
    private fun hideViews() {
        treeMarker.visibility = View.GONE
        treeEditButton.hide()
        cancelButton.hide()
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            treeMapViewModel.uiState.collect { mapViewState ->
                when (mapViewState) {
                    is TreeMapContract.MapViewState.Idle -> {
                    }
                    is TreeMapContract.MapViewState.MapState -> {
                        hideViews()
                        addTreeButton.show()
                        map.setOnCircleClickListener { treeCircle ->
                            disableSelectedCircle()
                            enableSelectedCircle(treeCircle)

                            val tag = treeCircle.tag as String
                            val treeEntity = treeMapViewModel.getTreeBy(id = tag)
                            previewTreeSpeciesText.text = "Порода ".plus(treeEntity.species.name.uppercase())
                            previewTreePosition.text = "Геопозиция ".plus("${treeEntity.coord.lat} ${treeEntity.coord.lon}")
                            previewTreeDiameter.text = "Диаметр кроны ".plus(treeEntity.diameter.toString())
                            previewTree.visibility = View.VISIBLE

                            treeDescriptionSpecies.text = "Порода ".plus(treeEntity.species.name.uppercase())
                            treeDescriptionPosition.text = "Геопозиция ".plus("${treeEntity.coord.lat} ${treeEntity.coord.lon}")
                            treeDescriptionDiameter.text = "Диаметр кроны ".plus(treeEntity.diameter.toString())
                        }
                    }
                    is TreeMapContract.MapViewState.MapErrorState -> {
                        hideViews()
                        addTreeButton.hide()
                        // show error text or picture
                    }
                    is TreeMapContract.MapViewState.MapPickTreeLocationState -> {
                        showViews()
                        addTreeButton.hide()
                        previewTree.visibility = View.GONE
                        map.setOnCircleClickListener { }
                        val cameraUpdateFactory =
                            CameraUpdateFactory.newLatLngZoom(map.cameraPosition.target, 18f)
                        map.animateCamera(cameraUpdateFactory)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            treeMapViewModel.treeDataState.collect { treeDataViewState ->
                when (treeDataViewState) {
                    is TreesViewState.Idle -> {
                    }
                    is TreesViewState.TreesLoadingState -> {
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
            lifecycleScope.launch {
                sharedViewModel.addNewTree(map.cameraPosition.target)
            }
        }

        cancelButton.setOnClickListener {
            lifecycleScope.launch {
                treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeCanceled)
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