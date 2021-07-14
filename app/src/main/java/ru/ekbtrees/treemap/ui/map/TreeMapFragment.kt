package ru.ekbtrees.treemap.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ktx.addCircle
import com.google.maps.android.ktx.addGroundOverlay
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreeMapBinding
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.SharedViewModel
import ru.ekbtrees.treemap.ui.utils.ClusterIconDrawer
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.mappers.LatLonMapper
import ru.ekbtrees.treemap.ui.model.RegionBoundsUIModel
import ru.ekbtrees.treemap.ui.mvi.contract.TreeMapContract
import kotlin.math.pow

@AndroidEntryPoint
class TreeMapFragment : Fragment() {

    lateinit var binding: FragmentTreeMapBinding

    private val treeMapViewModel: TreeMapViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var map: GoogleMap
    private var selectedCircle: Circle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreeMapBinding.inflate(inflater, container, false)

        binding.addTreeButton.setOnClickListener {
            lifecycleScope.launch {
                disableSelectedCircle()
                treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeButtonClicked)
            }
        }

        binding.previewCloseButton.setOnClickListener {
            disableSelectedCircle()
            binding.treePreview.visibility = View.GONE
        }

        binding.previewTreeDescriptionButton.setOnClickListener {
            Toast.makeText(requireContext(), "Show tree description.", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMap()

        binding.editTreeButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.addNewTree(map.cameraPosition.target)
                val navController = findNavController()
                val action = TreeMapFragmentDirections.actionTreeMapFragmentToEditTreeFragment(
                    EditTreeInstanceValue.TreeLocation(map.cameraPosition.target)
                )
                navController.navigate(action)
            }
        }

        binding.cancelButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeCanceled)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        treeMapViewModel.cameraPosition = map.cameraPosition
    }

    private fun disableSelectedCircle() {
        selectedCircle?.strokeColor = Color.BLACK
        selectedCircle?.strokeWidth = 1f
    }

    private fun enableSelectedCircle(treeCircle: Circle) {
        selectedCircle = treeCircle
        selectedCircle?.strokeColor = Color.RED
        selectedCircle?.strokeWidth = 5f
    }

    private fun loadTreesAtMap(items: Collection<TreeEntity>) {
        items.forEach { item ->
            val added = map.addCircle {
                center(LatLonMapper().map(item.coord))
                val radius = if (item.diameter != 0.0f) item.diameter.toDouble() / 2.0 else 3.0
                radius(radius)
                fillColor(item.species.color)
                clickable(true)
                strokeWidth(1f)
            }
            added.tag = item.id
        }
    }

    private fun loadClustersAtMap(items: Collection<ClusterTreesEntity>) {
        items.forEach { cluster ->
            map.addGroundOverlay {
                val iconDrawer = ClusterIconDrawer(Color.BLUE, 500, 500)
                val icon = iconDrawer.draw(cluster.count.toString())
                image(BitmapDescriptorFactory.fromBitmap(icon))
                position(LatLonMapper().map(cluster.coord), 500f)
            }
        }
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            map = mapFragment.awaitMap()
            setUpCamera()

            map.setOnCameraIdleListener {
                updateMapData()
            }
            observeViewModel()

            treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnMapViewReady)
        }
    }

    private fun setUpCamera() {
        map.setMinZoomPreference(MIN_ZOOM_LEVEL)
        if (treeMapViewModel.cameraPosition != null) {
            map.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    treeMapViewModel.cameraPosition ?: throw Exception()
                )
            )
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

    /**
     * Выводит на экран View объекты состояния добавления дерева
     * */
    private fun showViews() {
        binding.treeMarker.visibility = View.VISIBLE
        binding.editTreeButton.show()
        binding.cancelButton.show()
    }

    /**
     * Скарывает View объекты состояния добавления дерева
     * */
    private fun hideViews() {
        binding.treeMarker.visibility = View.GONE
        binding.editTreeButton.hide()
        binding.cancelButton.hide()
    }

    private fun getRegionBounds(cameraPosition: CameraPosition): RegionBoundsUIModel {
        val position = cameraPosition.target
        val alpha = 0.000125 * 2.0.pow(21 - cameraPosition.zoom.toDouble())
        val topLeft = LatLng(position.latitude + alpha, position.longitude - alpha)
        val bottomRight = LatLng(position.latitude - alpha, position.longitude + alpha)
        return RegionBoundsUIModel(topLeft, bottomRight)
    }

    private fun updateMapData() {
        lifecycleScope.launch {
            if (map.cameraPosition.zoom < 16) {
                treeMapViewModel.getClusterTreesInRegion(getRegionBounds(map.cameraPosition))
            } else {
                treeMapViewModel.uploadTreesInRegion(getRegionBounds(map.cameraPosition))
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            treeMapViewModel.uiState.collect { mapViewState ->
                when (mapViewState) {
                    is TreeMapContract.MapViewState.Idle -> {
                    }
                    is TreeMapContract.MapViewState.MapState -> {
                        hideViews()
                        binding.addTreeButton.show()
                        map.setOnCircleClickListener { treeCircle ->
                            disableSelectedCircle()
                            enableSelectedCircle(treeCircle)

                            val tag = treeCircle.tag as String
                            val treeEntity = treeMapViewModel.getTreeBy(id = tag)
                            binding.previewTreeSpeciesText.text = treeEntity.species.name
                            binding.treePreview.visibility = View.VISIBLE
                        }
                    }
                    is TreeMapContract.MapViewState.MapErrorState -> {
                        hideViews()
                        binding.addTreeButton.hide()
                        // show error text or picture
                    }
                    is TreeMapContract.MapViewState.MapPickTreeLocationState -> {
                        showViews()
                        binding.addTreeButton.hide()
                        binding.treePreview.visibility = View.GONE
                        map.setOnCircleClickListener { }
                        val cameraUpdateFactory =
                            CameraUpdateFactory.newLatLngZoom(map.cameraPosition.target, 18f)
                        map.animateCamera(cameraUpdateFactory)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            treeMapViewModel.treeDataState.collect { dataState ->
                when (dataState) {
                    is TreeMapContract.DataState.Idle -> {
                    }
                    is TreeMapContract.DataState.Loading -> {
                        map.clear()
                        binding.progressHorizontal.visibility = View.VISIBLE
                    }
                    is TreeMapContract.DataState.Loaded -> {
                        binding.progressHorizontal.visibility = View.GONE
                        when (dataState.data) {
                            is TreeMapContract.LoadedData.Trees -> {
                                loadTreesAtMap(dataState.data.trees)
                            }
                            is TreeMapContract.LoadedData.TreeClusters -> {
                                loadClustersAtMap(dataState.data.clusterTrees)
                            }
                        }
                    }
                    is TreeMapContract.DataState.Error -> {
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "TreeMapFragment"
        private val EKATERINBURG_CENTER_POSITION = LatLng(56.835378, 60.611970)
        private val EKATERINBURG_CAMERA_BOUNDS = LatLngBounds(
            LatLng(56.777584, 60.492406), // SW bounds
            LatLng(56.901152, 60.675740) // NE bounds
        )
        private const val MIN_ZOOM_LEVEL = 11f
    }
}