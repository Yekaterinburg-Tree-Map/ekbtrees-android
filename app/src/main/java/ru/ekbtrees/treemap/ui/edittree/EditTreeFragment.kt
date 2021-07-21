package ru.ekbtrees.treemap.ui.edittree

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentEditTreeBinding
import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeDetailEntity
import ru.ekbtrees.treemap.ui.mappers.LatLonMapper
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import java.util.*

private const val TAG = "EditTreeFragment"

/**
 * Фрагмент добавления или редактирования детализации дерева.
 * */
@AndroidEntryPoint
class EditTreeFragment : Fragment() {

    private val viewModel: EditTreeViewModel by viewModels()

    private lateinit var binding: FragmentEditTreeBinding

    private lateinit var map: GoogleMap
    private lateinit var location: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTreeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EditTreeFragmentArgs by navArgs()
        viewModel.provideInstanceValue(args.instanceValue)
        observeViewStates()

        binding.conditionAssessmentValue.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.conditionAssessmentTextValue.text = getString(
                    R.string.condition_assessment_holder, progress.toString()
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.saveData.setOnClickListener {
            viewModel.setEvent(
                EditTreeContract.EditTreeEvent.OnSaveButtonClicked(
                    TreeDetailEntity(
                        id = binding.treeIdValue.text.toString(),
                        coord = LatLonEntity(lat = location.latitude, lon = location.longitude),
                        species = SpeciesEntity(
                            id = "0",
                            color = Color.parseColor("#000000"),
                            name = binding.treeSpeciesValue.selectedItem.toString()
                        ),
                        height = if (!binding.heightOfTheFirstBranchValue.text.isNullOrBlank())
                            binding.heightOfTheFirstBranchValue.text.toString().toDouble()
                        else 0.0,
                        numberOfTrunks = if (!binding.numberOfTrunksValue.text.isNullOrBlank())
                            binding.numberOfTrunksValue.text.toString().toInt()
                        else 0,
                        trunkGirth = if (!binding.trunkGirthValue.text.isNullOrBlank())
                            binding.trunkGirthValue.text.toString().toDouble()
                        else 0.0,
                        diameterOfCrown = if (!binding.diameterOfCrownValue.text.isNullOrBlank())
                            binding.diameterOfCrownValue.text.toString().toDouble()
                        else 0.0,
                        heightOfTheFirstBranch = if (!binding.heightOfTheFirstBranchValue.text.isNullOrBlank())
                            binding.heightOfTheFirstBranchValue.text.toString().toDouble()
                        else 0.0,
                        conditionAssessment = binding.conditionAssessmentValue.progress,
                        age = if (!binding.ageValue.text.isNullOrBlank())
                            binding.ageValue.text.toString().toInt()
                        else 0,
                        treePlantingType = "",
                        createTime = binding.createTimeValue.text.toString(),
                        updateTime = binding.updateTimeValue.text.toString(),
                        authorId = 0,
                        status = binding.treeStatusValue.selectedItem.toString(),
                        fileIds = emptyList()
                    )
                )
            )
        }
    }

    /**
     * Выводит карту с местоположением дерева и заполняет поля координат.
     * */
    private fun setupTreeLocation(treeLocation: LatLng) {
        location = treeLocation
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
     * Заполняет все спиннеры.
     * */
    private fun setupSpinners(speciesName: String? = null) {
        viewLifecycleOwner.lifecycleScope.launch {
            val treeSpecies = viewModel.getTreeSpecies().map { it.name }.toMutableList()
            treeSpecies.add(0, getString(R.string.select_tree_species))
            val speciesSpinnerAdapter = createSpinnerAdapter(treeSpecies.toTypedArray())
            binding.treeSpeciesValue.adapter = speciesSpinnerAdapter

            if (speciesName != null) {
                val speciesId = treeSpecies.indexOf(speciesName)
                binding.treeSpeciesValue.setSelection(speciesId)
            }
        }

        val statusArray = resources.getStringArray(R.array.status_types)
        val statusSpinnerAdapter = createSpinnerAdapter(statusArray)
        binding.treeStatusValue.adapter = statusSpinnerAdapter
    }

    private fun createSpinnerAdapter(array: Array<String>): ArrayAdapter<String> {
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return arrayAdapter
    }

    /**
     * Выводит всю полученную информацию.
     * */
    private fun setupTreeData(treeDetail: TreeDetailEntity) {
        setupTreeLocation(treeLocation = LatLonMapper().map(treeDetail.coord))
        setupSpinners(treeDetail.species.name)

        showTextInEditText(binding.numberOfTrunksValue, treeDetail.numberOfTrunks.toString())
        showTextInEditText(binding.trunkGirthValue, treeDetail.trunkGirth.toString())
        showTextInEditText(binding.diameterOfCrownValue, treeDetail.diameterOfCrown.toString())
        showTextInEditText(binding.ageValue, treeDetail.age.toString())
        showTextInEditText(
            binding.heightOfTheFirstBranchValue,
            treeDetail.heightOfTheFirstBranch.toString()
        )
        if (binding.conditionAssessmentTextValue.text.toString().isEmpty()) {
            binding.conditionAssessmentValue.progress = treeDetail.conditionAssessment
        }
        binding.conditionAssessmentTextValue.text = getString(
            R.string.condition_assessment_holder,
            binding.conditionAssessmentValue.progress.toString()
        )
        // tree planting type

        binding.treeIdValue.text = treeDetail.id
        binding.authorValue.text = treeDetail.authorId.toString()
        binding.createTimeValue.text = treeDetail.createTime
        binding.updateTimeValue.text = treeDetail.updateTime
    }

    private fun showTextInEditText(editText: TextInputEditText, text: String) {
        if (editText.text.toString().isEmpty()) {
            editText.setText(text)
        }
    }

    /**
     * Заполняет только спинеры и выставляет заглушки.
     * */
    private fun setupEmptyTreeData() {
        setupSpinners()

        binding.conditionAssessmentTextValue.text =
            getString(
                R.string.condition_assessment_holder,
                binding.conditionAssessmentValue.progress.toString()
            )

        binding.treeIdValue.text = UUID.randomUUID().toString()
        binding.createTimeValue.text = Calendar.getInstance(Locale.ROOT).time.toString()
        binding.updateTimeValue.text = Calendar.getInstance(Locale.ROOT).time.toString()
    }

    private fun observeViewStates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { editTreeViewState ->
                when (editTreeViewState) {
                    is EditTreeContract.EditTreeViewState.Idle -> {
                    }
                    is EditTreeContract.EditTreeViewState.EmptyData -> {
                        setupTreeLocation(treeLocation = editTreeViewState.treeLocation)
                        setupEmptyTreeData()
                    }
                    is EditTreeContract.EditTreeViewState.DataLoading -> {
                        // Show progressBar
                    }
                    is EditTreeContract.EditTreeViewState.DataLoaded -> {
                        setupTreeData(treeDetail = editTreeViewState.treeData)
                    }
                    is EditTreeContract.EditTreeViewState.Error -> {
                        // Show error message and show reload data button
                    }
                }
            }
        }
    }
}