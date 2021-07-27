package ru.ekbtrees.treemap.ui.edittree

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.SpeciesUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import java.sql.Timestamp

private const val TAG = "EditTreeFragment"

/**
 * Фрагмент добавления или редактирования детализации дерева.
 * */
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

        binding.changeLocationButton.setOnClickListener {
            onChangeLocation()
        }

        binding.saveData.setOnClickListener {
            val treeDetail: EditTreeContract.TreeDetailFragmentModel =
                when (viewModel.currentState) {
                    is EditTreeContract.EditTreeViewState.DataLoaded -> {
                        EditTreeContract.TreeDetailFragmentModel.TreeDetail(getTreeDetail())
                    }
                    is EditTreeContract.EditTreeViewState.NewTreeData -> {
                        EditTreeContract.TreeDetailFragmentModel.NewTreeDetail(getNewTreeDetail())
                    }
                    else -> return@setOnClickListener
                }
            viewModel.setEvent(EditTreeContract.EditTreeEvent.OnSaveButtonClicked(treeDetail))
        }

        binding.trunkGirthValue.addTextChangedListener {
            if (!binding.trunkGirthValue.text.isNullOrBlank()) {
                val diameter = binding.trunkGirthValue.text.toString().toDouble() / Math.PI
                binding.diameterOfCrownValue.setText(String.format("%.2f", diameter))
            } else {
                binding.diameterOfCrownValue.text?.clear()
            }
        }
    }

    /**
     * Собирает данные с полей ввода и переходит к фрагменту смены местоположения.
     * Работает независимо от состояния view.
     * */
    private fun onChangeLocation() {
        val treeId = if (binding.treeIdValue.text != getString(R.string.tree_id_plug))
            binding.treeIdValue.text.toString()
        else ""
        val treeLocation: LatLng
        val createTime: String
        val updateTime: String
        val authorId: Int
        when (val state = viewModel.currentState) {
            is EditTreeContract.EditTreeViewState.NewTreeData -> {
                treeLocation = state.treeDetail.coord
                createTime = state.treeDetail.createTime
                updateTime = state.treeDetail.updateTime
                authorId = state.treeDetail.authorId
            }
            is EditTreeContract.EditTreeViewState.DataLoaded -> {
                treeLocation = state.treeData.coord
                createTime = state.treeData.createTime
                updateTime = state.treeData.updateTime
                authorId = state.treeData.authorId
            }
            else -> {
                return
            }
        }
        val treeDetail = TreeDetailUIModel(
            id = treeId,
            coord = treeLocation,
            species = SpeciesUIModel(
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
            createTime = createTime,
            updateTime = updateTime,
            authorId = authorId,
            status = binding.treeStatusValue.selectedItem.toString(),
            fileIds = emptyList()
        )
        val navController = findNavController()
        val action = EditTreeFragmentDirections
            .actionEditTreeFragmentToChangeLocationFragment(treeDetail)
        navController.navigate(action)
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
    private fun showTreeDetailData(treeDetail: TreeDetailUIModel) {
        setupTreeLocation(treeLocation = treeDetail.coord)
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

        val treeId = if (treeDetail.id == "") getString(R.string.tree_id_plug) else treeDetail.id
        binding.treeIdValue.text = treeId
        binding.authorValue.text = treeDetail.authorId.toString()
        binding.createTimeValue.text = treeDetail.createTime
        binding.updateTimeValue.text = treeDetail.updateTime
    }

    /**
     * Выводит текст в TextInputEditText, если поле ранее не было заполнено.
     * */
    private fun showTextInEditText(editText: TextInputEditText, text: String) {
        if (editText.text.toString().isEmpty() && text !in listOf("0", "0.0")) {
            editText.setText(text)
        }
    }

    /**
     * @return Объект класса [TreeDetailUIModel] с заполненными данными
     * @throws IllegalStateException функция вызвана вне состояния NewTreeDetail
     * */
    private fun getNewTreeDetail(): NewTreeDetailUIModel {
        val state = viewModel.currentState
        if (state !is EditTreeContract.EditTreeViewState.NewTreeData) {
            throw IllegalStateException()
        }
        return NewTreeDetailUIModel(
            coord = state.treeDetail.coord,
            species = SpeciesUIModel(
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
            createTime = state.treeDetail.createTime,
            updateTime = state.treeDetail.updateTime,
            authorId = state.treeDetail.authorId,
            status = binding.treeStatusValue.selectedItem.toString(),
            fileIds = emptyList()
        )
    }

    /**
     * Забирает данные из всех полей
     * @return Объект класса [TreeDetailUIModel] с заполненными данными
     * @throws IllegalStateException функция вызвана вне состояния DataLoaded
     * */
    private fun getTreeDetail(): TreeDetailUIModel {
        val state = viewModel.currentState
        if (state !is EditTreeContract.EditTreeViewState.DataLoaded) {
            throw IllegalStateException("")
        }
        return TreeDetailUIModel(
            id = state.treeData.id,
            coord = state.treeData.coord,
            species = SpeciesUIModel(
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
            createTime = state.treeData.createTime,
            updateTime = System.currentTimeMillis().toString(),
            authorId = state.treeData.authorId,
            status = binding.treeStatusValue.selectedItem.toString(),
            fileIds = emptyList()
        )
    }

    /**
     * Заполняет только спинеры и выставляет заглушки.
     * */
    private fun showNewTreeDetailData(newTreeDetail: NewTreeDetailUIModel) {
        setupTreeLocation(newTreeDetail.coord)
        setupSpinners(newTreeDetail.species?.name)

        binding.conditionAssessmentTextValue.text =
            getString(
                R.string.condition_assessment_holder,
                binding.conditionAssessmentValue.progress.toString()
            )

        binding.treeIdValue.text = getString(R.string.tree_id_plug)
        val date = Timestamp(System.currentTimeMillis())
        binding.createTimeValue.text = date.toString()
        binding.updateTimeValue.text = System.currentTimeMillis().toString()
    }

    private fun observeViewStates() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { editTreeViewState ->
                when (editTreeViewState) {
                    is EditTreeContract.EditTreeViewState.Idle -> {
                    }
                    is EditTreeContract.EditTreeViewState.NewTreeData -> {
                        showNewTreeDetailData(editTreeViewState.treeDetail)
                    }
                    is EditTreeContract.EditTreeViewState.DataLoading -> {
                        // Show progressBar
                    }
                    is EditTreeContract.EditTreeViewState.DataLoaded -> {
                        showTreeDetailData(treeDetail = editTreeViewState.treeData)
                    }
                    is EditTreeContract.EditTreeViewState.Error -> {
                        // Show error message and show reload data button
                    }
                }
            }
        }
    }
}