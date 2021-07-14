package ru.ekbtrees.treemap.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreeMapBinding
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.SharedViewModel
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.mappers.LatLonMapper
import ru.ekbtrees.treemap.ui.mvi.contract.TreeMapContract
import ru.ekbtrees.treemap.ui.viewstates.TreesViewState
import java.util.*

@AndroidEntryPoint
class TreeMapFragment : Fragment() {

    private lateinit var binding: FragmentTreeMapBinding

    private lateinit var map: GoogleMap
    private lateinit var locationProvider: LocationProvider
    private lateinit var addTreeButton: FloatingActionButton

    // pick tree location state
    private lateinit var treeMarker: ImageView
    private lateinit var treeEditButton: FloatingActionButton
    private lateinit var cancelButton: FloatingActionButton

    // tree preview
    private lateinit var treePreview: CardView
    private lateinit var previewTreeSpeciesText: TextView
    private lateinit var previewTreePosition: TextView
    private lateinit var previewTreeDiameter: TextView
    private lateinit var previewCloseButton: ImageButton
    private lateinit var previewShowDescriptionButton: MaterialButton

    private val treeMapViewModel: TreeMapViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                handleLocationPermissionResponse(isGranted)
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTreeMapBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            sharedViewModel.permissionResultReceiver.collect { (requestCode, isGranted) ->
                if (requestCode == LOCATION_REQUEST_CODE) {
                    handleLocationPermissionResponse(isGranted)
                }
            }
        }

        binding.addTreeButton.setOnClickListener {
            when (treeMapViewModel.currentState) {
                is TreeMapContract.MapViewState.MapState -> {
                    disableSelectedCircle()
                    treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnAddTreeLaunched)
                }
                is TreeMapContract.MapViewState.MapPickTreeLocationState -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        sharedViewModel.addNewTree(map.cameraPosition.target)
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
            Toast.makeText(requireContext(), "Show tree description.", Toast.LENGTH_SHORT).show()
        }

        binding.userLocationButton.setOnClickListener {
            if (!::map.isInitialized) return@setOnClickListener
            if (isUserLocationGranted) {
                val position = locationProvider.lastLocation
                val cameraUpdate =
                    CameraUpdateFactory.newLatLng(position)
                map.animateCamera(cameraUpdate)
            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
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

        handleLocationPermissionResponse(isUserLocationGranted)
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

    private fun handleLocationPermissionResponse(isGranted: Boolean) {
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
        if (::map.isInitialized && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
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

            observeViewModel()

            treeMapViewModel.setEvent(TreeMapContract.TreeMapEvent.OnMapViewReady)
        }
    }

    private fun setUpCamera() {
        map.setLatLngBoundsForCameraTarget(EKATERINBURG_CAMERA_BOUNDS)
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
                            binding.previewTreeSpeciesText.text = treeEntity.species.name
                            binding.treePreview.visibility = View.VISIBLE
                            previewTreeSpeciesText.text = treeEntity.species.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                            previewTreePosition.text =
                                getString(R.string.tree_location).plus(" ${treeEntity.coord.lat} ${treeEntity.coord.lon}")
                            previewTreeDiameter.text =
                                getString(R.string.diameter_of_crown).plus(" ${treeEntity.diameter}")
                            treePreview.visibility = View.VISIBLE
                        }
                        binding.addTreeButton.setImageDrawable(
                            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_add_24)
                        )
                        binding.addTreeButton.imageTintList =
                            AppCompatResources.getColorStateList(requireContext(), R.color.black)
                    }
                    is TreeMapContract.MapViewState.MapErrorState -> {
                        hidePickTreeLocationViews()
                        binding.addTreeButton.hide()
                        // show error text or picture
                    }
                    is TreeMapContract.MapViewState.MapPickTreeLocationState -> {
                        showPickTreeLocationViews()
                        binding.addTreeButton.setImageDrawable(
                            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_check_24)
                        )
                        binding.addTreeButton.imageTintList =
                            AppCompatResources.getColorStateList(requireContext(), R.color.green)
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

    companion object {
        private const val TAG = "TreeMapFragment"
        const val LOCATION_REQUEST_CODE = 0
        private val EKATERINBURG_CENTER_POSITION = LatLng(56.835378, 60.611970)
        private val EKATERINBURG_CAMERA_BOUNDS = LatLngBounds(
            LatLng(56.777584, 60.492406), // SW bounds
            LatLng(56.901152, 60.675740) // NE bounds
        )
        private const val MIN_ZOOM_LEVEL = 11f
    }
}