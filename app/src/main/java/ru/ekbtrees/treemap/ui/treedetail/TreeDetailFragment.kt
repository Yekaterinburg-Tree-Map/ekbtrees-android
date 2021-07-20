package ru.ekbtrees.treemap.ui.treedetail

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreeDetailBinding
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract

private const val TAG = "TreeDetailFragment"

/**
 * Фрагмент детализауии дерева.
 * */
@AndroidEntryPoint
class TreeDetailFragment : Fragment() {
    private lateinit var treeLocation: LatLng
    private lateinit var treeId: String
    private lateinit var map: GoogleMap

    private val treeDetailViewModel: TreeDetailViewModel by viewModels()

    private lateinit var binding: FragmentTreeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: TreeDetailFragmentArgs by navArgs()
        treeId = args.treeId
        treeDetailViewModel.provideTreeId(treeId)
        observeViewModel()
        binding.editTreeButton.setOnClickListener {
            val navController = findNavController()
            val instanceValue = EditTreeInstanceValue.TreeId(treeId)
            val action = TreeDetailFragmentDirections.actionTreeDetailFragmentToEditTreeFragment(
                instanceValue
            )
            navController.navigate(action)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            treeDetailViewModel.uiState.collect { viewState ->
                when (viewState) {
                    is TreeDetailContract.TreeDetailState.Idle -> {
                    }
                    is TreeDetailContract.TreeDetailState.Loading -> {
                        // Show progress bar
                    }
                    is TreeDetailContract.TreeDetailState.Loaded -> {
                        val mapFragment =
                            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                        mapFragment?.getMapAsync { googleMap ->
                            map = googleMap
                            treeLocation = LatLng(
                                viewState.treeDetailEntity.coord.lat,
                                viewState.treeDetailEntity.coord.lon
                            )
                            val circleOptions = CircleOptions().apply {
                                center(treeLocation)
                                radius(viewState.treeDetailEntity.diameterOfCrown / 2.0)
                                fillColor(viewState.treeDetailEntity.species.color)
                                strokeColor(Color.BLACK)
                                strokeWidth(2f)
                            }
                            map.addCircle(circleOptions)
                            val zoomLevel = 19f
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    treeLocation,
                                    zoomLevel
                                )
                            )
                        }
                        binding.latitudeValue.text = viewState.treeDetailEntity.coord.lat.toString()
                        binding.longitudeValue.text =
                            viewState.treeDetailEntity.coord.lon.toString()
                        binding.treeSpeciesValue.text = viewState.treeDetailEntity.species.name
                        binding.diameterValue.text =
                            viewState.treeDetailEntity.diameterOfCrown.toString()
                        binding.numberOfTrunks.text =
                            viewState.treeDetailEntity.numberOfTrunks.toString()
                        binding.trunkGirthValue.text =
                            viewState.treeDetailEntity.trunkGirth.toString()
                        binding.ageValue.text = viewState.treeDetailEntity.age.toString()
                        binding.heightOfTheFirstBranchValue.text =
                            viewState.treeDetailEntity.heightOfTheFirstBranch.toString()
                        binding.plantingTypeValue.text = viewState.treeDetailEntity.treePlantingType
                    }
                    is TreeDetailContract.TreeDetailState.Error -> {
                        // Show error
                    }
                }
            }
        }
    }
}