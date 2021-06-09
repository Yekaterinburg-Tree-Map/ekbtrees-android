package ru.ekbtrees.treemap.ui.edittree

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentEditTreeBinding
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.LatLonMapper
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract

private const val TAG = "EditTreeFragment"

@AndroidEntryPoint
class EditTreeFragment : Fragment() {

    private val viewModel: EditTreeViewModel by viewModels()

    private lateinit var binding: FragmentEditTreeBinding

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTreeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewStates()
    }

    /**
     * Выводит карту с местоположением дерева и заполняет поля координат.
     * */
    private fun setupTreeLocation(treeLocation: LatLng) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
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

            binding.latitudeValue.text = String.format("%.8f", treeLocation.latitude)
            binding.longitudeValue.text = String.format("%.8f", treeLocation.longitude)
        }
    }

    /**
     * Выводит всю полученную информацию.
     * */
    private fun setupTreeData(treeDetail: TreeDetailEntity) {
        setupTreeLocation(treeLocation = LatLonMapper().map(treeDetail.coord))
        val treeSpecies = viewModel.getTreeSpecies().map { it.name }.toMutableList()
        treeSpecies.add(0, getString(R.string.select_tree_species))
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            treeSpecies
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treeSpeciesValue.adapter = spinnerAdapter
    }

    private fun observeViewStates() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { editTreeViewState ->
                when (editTreeViewState) {
                    is EditTreeContract.EditTreeViewState.Idle -> {
                    }
                    is EditTreeContract.EditTreeViewState.EmptyTreeDataState -> {
                        setupTreeLocation(treeLocation = editTreeViewState.treeLocation)
                    }
                    is EditTreeContract.EditTreeViewState.TreeDataLoadingState -> {
                        // Show progressBar
                    }
                    is EditTreeContract.EditTreeViewState.TreeDataLoadedState -> {
                        setupTreeData(treeDetail = editTreeViewState.treeData)
                    }
                    is EditTreeContract.EditTreeViewState.TreeDataLoadingFailedState -> {
                        // Show error message and show reload data button
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(instanceValue: EditTreeInstanceValue) =
            EditTreeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(INSTANCE_VALUE_KEY, instanceValue)
                }
            }
    }
}