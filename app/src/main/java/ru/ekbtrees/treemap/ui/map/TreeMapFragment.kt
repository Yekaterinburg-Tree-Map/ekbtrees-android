package ru.ekbtrees.treemap.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.addCircle
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreeMapBinding
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.mappers.LatLonMapper
import ru.ekbtrees.treemap.ui.model.RegionBoundsUIModel
import ru.ekbtrees.treemap.ui.mvi.contract.TreeMapContract
import java.util.*

/**
 * Фрагмент карты деревьев
 * */
@AndroidEntryPoint
class TreeMapFragment : Fragment() {

    private lateinit var binding: FragmentTreeMapBinding

    private val treeMapViewModel: TreeMapViewModel by viewModels()

    private lateinit var locationProvider: LocationProvider
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<TreeMapClusterManagerBuilder.TreeClusterItem>

    private var selectedCircle: Circle? = null
    private var isUserLocationGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = LocationProvider(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isUserLocationGranted = true
        } else {
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (!isGranted) {
                        Toast.makeText(
                            requireContext(),
                            getText(R.string.location_access_denied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    handleLocationPermissionResponse(isGranted)
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTreeMapBinding.inflate(inflater, container, false)

        binding.addTreeButton.setOnClickListener {
            when (treeMapViewModel.currentState) {
                is TreeMapContract.MapViewState.MapState -> {
                    disableSelectedCircle()
                    treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeButtonClicked)
                }
                is TreeMapContract.MapViewState.MapPickTreeLocationState -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val navController = findNavController()
                        val action =
                            TreeMapFragmentDirections.actionTreeMapFragmentToEditTreeFragment(
                                EditTreeInstanceValue.TreeLocation(map.cameraPosition.target)
                            )
                        navController.navigate(action)
                    }
                }
                else -> {
                }
            }
        }

        binding.previewCloseButton.setOnClickListener {
            disableSelectedCircle()
            binding.treePreview.visibility = View.GONE
        }

        binding.previewTreeDescriptionButton.setOnClickListener {
            val navController = findNavController()
            val action =
                TreeMapFragmentDirections.actionTreeMapFragmentToTreeDetailFragment(selectedCircle?.tag.toString())
            navController.navigate(action)
        }

        binding.userLocationButton.setOnClickListener {
            if (!::map.isInitialized) return@setOnClickListener
            if (isUserLocationGranted) {
                val position = locationProvider.lastLocation
                val cameraUpdate = CameraUpdateFactory.newLatLng(position)
                map.animateCamera(cameraUpdate)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpMap()

        binding.cancelButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeCanceled)
            }
        }

        handleLocationPermissionResponse(isUserLocationGranted, false)
    }

    override fun onResume() {
        super.onResume()
        locationProvider.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        locationProvider.stopLocationUpdates()
        if (::map.isInitialized) {
            treeMapViewModel.cameraPosition = map.cameraPosition
        }
    }

    private fun handleLocationPermissionResponse(
        isGranted: Boolean,
        moveCameraToUser: Boolean = true
    ) {
        isUserLocationGranted = isGranted
        if (isGranted) {
            binding.userLocationButton.setImageResource(R.drawable.ic_location_24)
            binding.userLocationButton.imageTintList =
                AppCompatResources.getColorStateList(requireContext(), R.color.black)
        } else {
            binding.userLocationButton.setImageResource(R.drawable.ic_location_disabled_24)
            binding.userLocationButton.imageTintList =
                AppCompatResources.getColorStateList(requireContext(), R.color.red)
        }
        if (::map.isInitialized && moveCameraToUser && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            val userLocation = locationProvider.fetchUserLocation()
            userLocation.addOnSuccessListener { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                map.animateCamera(cameraUpdate)
            }
        }
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

    private fun loadClustersAtMap(items: Collection<TreeEntity>) {
        items.forEach { tree ->
            val clusterItem =
                TreeMapClusterManagerBuilder.TreeClusterItem(LatLonMapper().map(tree.coord))
            clusterManager.addItem(clusterItem)
        }
        clusterManager.cluster()
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            map = mapFragment.awaitMap()
            map.setOnMyLocationButtonClickListener {
                false
            }
            map.uiSettings.isMyLocationButtonEnabled = false
            map.setLocationSource(locationProvider)

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                map.isMyLocationEnabled = true
            }
            setUpCamera()
            clusterManager = TreeMapClusterManagerBuilder.buildClusterManager(requireContext(), map)

                map.setOnCameraIdleListener {
                    updateMapData()
                    clusterManager
                }
                observeViewModel()

                treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnMapViewReady)
            }
        }

    private fun setUpCamera() {
        map.setLatLngBoundsForCameraTarget(EKATERINBURG_CAMERA_BOUNDS)
        map.setMinZoomPreference(MIN_ZOOM_LEVEL)
        if (treeMapViewModel.cameraPosition != null) {
            map.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    treeMapViewModel.cameraPosition ?: throw Exception()
                )
            )
        } else {
            if (isUserLocationGranted) {
                locationProvider.fetchUserLocation().addOnSuccessListener { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
            } else {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        EKATERINBURG_CENTER_POSITION,
                        MIN_ZOOM_LEVEL
                    )
                )
            }
        }
    }

    /**
     * Выводит на экран View объекты состояния добавления дерева
     * */
    private fun showPickTreeLocationViews() {
        binding.treeMarker.visibility = View.VISIBLE
        binding.cancelButton.show()
    }

    /**
     * Скарывает View объекты состояния добавления дерева
     * */
    private fun hidePickTreeLocationViews() {
        binding.treeMarker.visibility = View.GONE
        binding.cancelButton.hide()
    }

    private fun updateMapData() {
        val visibleRegion = map.projection.visibleRegion.latLngBounds
        val topLeft = LatLng(visibleRegion.southwest.latitude, visibleRegion.northeast.longitude)
        val botRight = LatLng(visibleRegion.northeast.latitude, visibleRegion.southwest.longitude)
        val regionBounds = RegionBoundsUIModel(topLeft, botRight)
        lifecycleScope.launch {
            if (map.cameraPosition.zoom < 16) {
                treeMapViewModel.getClusterTreesInRegion(regionBounds)
            } else {
                treeMapViewModel.getTreesInRegion(regionBounds)
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
                        hidePickTreeLocationViews()
                        map.setOnCircleClickListener { treeCircle ->
                            disableSelectedCircle()
                            enableSelectedCircle(treeCircle)
                            val tag = treeCircle.tag as String
                            val treeEntity = treeMapViewModel.getTreeBy(id = tag)
                            binding.previewTreeSpeciesText.text =
                                treeEntity.species.name.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }
                            binding.previewTreeLocationValue.text =
                                getString(
                                    R.string.tree_location_holder,
                                    treeEntity.coord.lat.toString(),
                                    treeEntity.coord.lon.toString()
                                )
                            binding.previewTreeLocationValue.text =
                                getString(R.string.tree_location).plus(" ${treeEntity.coord.lat} ${treeEntity.coord.lon}")
                            binding.previewTreeDiameter.text =
                                getString(R.string.diameter_of_crown).plus(" ${treeEntity.diameter}")
                            binding.treePreview.visibility = View.VISIBLE
                        }
                        binding.addTreeButton.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.ic_add_24
                            )
                        )
                        binding.addTreeButton.imageTintList =
                            AppCompatResources.getColorStateList(
                                requireContext(),
                                R.color.black
                            )
                    }
                    is TreeMapContract.MapViewState.MapErrorState -> {
                        hidePickTreeLocationViews()
                        binding.addTreeButton.hide()
                        // show error text or picture
                    }
                    is TreeMapContract.MapViewState.MapPickTreeLocationState -> {
                        showPickTreeLocationViews()
                        binding.addTreeButton.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.ic_check_24
                            )
                        )
                        binding.addTreeButton.imageTintList =
                            AppCompatResources.getColorStateList(requireContext(), R.color.green)
                            AppCompatResources.getColorStateList(
                                requireContext(),
                                R.color.green
                            )
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
                        clusterManager.clearItems()
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