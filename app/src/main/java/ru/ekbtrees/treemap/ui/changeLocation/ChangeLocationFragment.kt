package ru.ekbtrees.treemap.ui.changeLocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentChangeLocationBinding
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel

private const val CAMERA_POSITION_KEY = "camera_position_key"
private val EKATERINBURG_CAMERA_BOUNDS = LatLngBounds(
    LatLng(56.777584, 60.492406), // SW bounds
    LatLng(56.901152, 60.675740) // NE bounds
)
private const val MIN_ZOOM_LEVEL = 11f

/**
 * Фрагмент изменения местоположения дерева.
 * */
@AndroidEntryPoint
class ChangeLocationFragment : Fragment() {

    private lateinit var binding: FragmentChangeLocationBinding

    private val navController: NavController by lazy { findNavController() }

    private lateinit var map: GoogleMap
    private lateinit var treeDetail: TreeDetailUIModel

    private lateinit var cameraPosition: CameraPosition

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeLocationBinding.inflate(inflater, container, false)

        // Пересоздаём TreeDetailUIModel с изменённым местоположением.
        binding.confirmButton.setOnClickListener {
            onConfirmButtonClicked()
        }

        binding.cancelButton.setOnClickListener {
            navController.popBackStack()
        }

        return binding.root
    }

    private fun onConfirmButtonClicked() {
        val newTreeLocation = map.cameraPosition.target
        val changedTreeDetail = TreeDetailUIModel(
            id = treeDetail.id,
            coord = newTreeLocation,
            species = treeDetail.species,
            height = treeDetail.height,
            numberOfTrunks = treeDetail.numberOfTrunks,
            trunkGirth = treeDetail.trunkGirth,
            diameterOfCrown = treeDetail.diameterOfCrown,
            heightOfTheFirstBranch = treeDetail.heightOfTheFirstBranch,
            conditionAssessment = treeDetail.conditionAssessment,
            age = treeDetail.age,
            treePlantingType = treeDetail.treePlantingType,
            createTime = treeDetail.createTime,
            updateTime = treeDetail.updateTime,
            authorId = treeDetail.authorId,
            status = treeDetail.status,
            fileIds = treeDetail.fileIds
        )
        val action =
            ChangeLocationFragmentDirections.actionChangeLocationFragmentToEditTreeFragment(
                EditTreeInstanceValue.NewTreeLocation(changedTreeDetail)
            )
        navController.navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: ChangeLocationFragmentArgs by navArgs()
        treeDetail = args.treeDetail

        cameraPosition = savedInstanceState?.getParcelable(CAMERA_POSITION_KEY)
            ?: CameraPosition.fromLatLngZoom(treeDetail.coord, 18f)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            map = mapFragment.awaitMap()

            map.setLatLngBoundsForCameraTarget(EKATERINBURG_CAMERA_BOUNDS)
            map.setMinZoomPreference(MIN_ZOOM_LEVEL)

            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(CAMERA_POSITION_KEY, map.cameraPosition)
    }
}