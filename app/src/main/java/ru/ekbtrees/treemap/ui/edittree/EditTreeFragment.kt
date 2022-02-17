package ru.ekbtrees.treemap.ui.edittree

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentEditTreeBinding
import ru.ekbtrees.treemap.ui.common.TreePhotosAdapter
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.PhotoUiModel
import ru.ekbtrees.treemap.ui.model.SpeciesUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel
import ru.ekbtrees.treemap.ui.mvi.contract.EditTreeContract
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "EditTreeFragment"

/**
 * Фрагмент добавления или редактирования детализации дерева.
 * */
@AndroidEntryPoint
class EditTreeFragment : Fragment(), BottomSheetImagePicker.OnImagesSelectedListener {

    private val viewModel: EditTreeViewModel by viewModels()

    private lateinit var binding: FragmentEditTreeBinding

    private lateinit var map: GoogleMap

    private lateinit var photoAdapter: TreePhotosAdapter

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
        observeViewModel()
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

        binding.getPhotoButton.setOnClickListener {
            BottomSheetImagePicker.Builder(getString(R.xml.files_paths)).apply {
                multiSelect()
                cameraButton(ButtonType.Button)
                show(childFragmentManager)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.reloadTreeDetailButton.setOnClickListener {
            viewModel.setEvent(EditTreeContract.EditTreeEvent.OnReloadButtonClicked)
        }

        binding.changeLocationButton.setOnClickListener {
            onChangeLocation()
        }

        binding.saveDataButton.setOnClickListener {
            lifecycleScope.launch {
                if (!checkInputFields()) {
                    return@launch
                }
                val treeDetail: EditTreeContract.TreeDetailFragmentModel =
                    when (viewModel.currentState) {
                        is EditTreeContract.EditTreeViewState.DataLoaded -> {
                            EditTreeContract.TreeDetailFragmentModel.TreeDetail(getTreeDetail())
                        }
                        is EditTreeContract.EditTreeViewState.NewTreeData -> {
                            EditTreeContract.TreeDetailFragmentModel.NewTreeDetail(getNewTreeDetail())
                        }
                        else -> return@launch
                    }
                viewModel.setEvent(EditTreeContract.EditTreeEvent.OnSaveButtonClicked(treeDetail))
            }
        }

        binding.trunkGirthValue.addTextChangedListener {
            if (!binding.trunkGirthValue.text.isNullOrBlank()) {
                val diameter = binding.trunkGirthValue.text.toString().toDouble() / Math.PI
                binding.diameterOfCrownValue.setText(String.format("%.2f", diameter))
            } else {
                binding.diameterOfCrownValue.text?.clear()
            }
        }

        photoAdapter = TreePhotosAdapter(
            onItemClick = { photoUri ->
                val list =
                    (viewModel.uiState.value as EditTreeContract.EditTreeViewState.NewTreeData).photoList.map { (it as PhotoUiModel.Photo).photoUrl }
                val action =
                    EditTreeFragmentDirections.actionEditTreeFragmentToTreePhotoViewFragment(list.toTypedArray())
                findNavController().navigate(action)
                Toast.makeText(
                    requireContext(),
                    "Photo Uri: $photoUri",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDeleteButtonClick = {
                viewModel.handleEvent(
                    EditTreeContract.EditTreeEvent.OnDeletePhoto(
                        filePath = it.second,
                        position = it.first
                    )
                )
            })
        binding.photos.adapter = photoAdapter
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
        val authorId: Int?
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

        val plantingTypeArray = resources.getStringArray(R.array.planting_types)
        val plantingTypeSpinnerAdapter = createSpinnerAdapter(plantingTypeArray)
        binding.plantingTypeValue.adapter = plantingTypeSpinnerAdapter

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
            binding.conditionAssessmentValue.progress = treeDetail.conditionAssessment ?: 0
        }
        binding.conditionAssessmentTextValue.text = getString(
            R.string.condition_assessment_holder,
            binding.conditionAssessmentValue.progress.toString()
        )

        val treeId = if (treeDetail.id == "") getString(R.string.tree_id_plug) else treeDetail.id
        binding.treeIdValue.text = treeId
        binding.authorValue.text = treeDetail.authorId.toString()
        binding.createTimeValue.text = formatTextTime(treeDetail.createTime)
        binding.updateTimeValue.text = formatTextTime(treeDetail.updateTime)
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
     * Собирает введённые пользоваетелем данные и обёртывет их в класс.
     * @return Объект класса [TreeDetailUIModel] с заполненными данными
     * @throws IllegalStateException функция вызвана вне состояния NewTreeDetail
     * @see [checkInputFields]
     * */
    private suspend fun getNewTreeDetail(): NewTreeDetailUIModel {
        val state = viewModel.currentState
        if (state !is EditTreeContract.EditTreeViewState.NewTreeData) {
            throw IllegalStateException()
        }
        return NewTreeDetailUIModel(
            coord = state.treeDetail.coord,
            species = viewModel.getSpeciesByName(binding.treeSpeciesValue.selectedItem.toString())!!,
            height = if (!binding.heightOfTheFirstBranchValue.text.isNullOrBlank())
                binding.heightOfTheFirstBranchValue.text.toString().toDouble()
            else null,
            numberOfTrunks = if (!binding.numberOfTrunksValue.text.isNullOrBlank())
                binding.numberOfTrunksValue.text.toString().toInt()
            else null,
            trunkGirth = if (!binding.trunkGirthValue.text.isNullOrBlank())
                binding.trunkGirthValue.text.toString().toDouble()
            else null,
            diameterOfCrown = if (!binding.diameterOfCrownValue.text.isNullOrBlank())
                binding.diameterOfCrownValue.text.toString().toDouble()
            else throw IllegalArgumentException("Поле ${binding.diameterOfCrownValue::class.simpleName} не должно быть пустым!"),
            heightOfTheFirstBranch = if (!binding.heightOfTheFirstBranchValue.text.isNullOrBlank())
                binding.heightOfTheFirstBranchValue.text.toString().toDouble()
            else null,
            conditionAssessment = binding.conditionAssessmentValue.progress,
            age = if (!binding.ageValue.text.isNullOrBlank())
                binding.ageValue.text.toString().toInt()
            else null,
            treePlantingType = if (binding.plantingTypeValue.selectedItem.toString()
                != resources.getStringArray(R.array.planting_types)[0]
            ) {
                binding.plantingTypeValue.selectedItem.toString()
            } else null,
            createTime = state.treeDetail.createTime,
            updateTime = state.treeDetail.updateTime,
            authorId = state.treeDetail.authorId,
            status = if (binding.treeStatusValue.selectedItem.toString()
                != resources.getStringArray(R.array.status_types)[0]
            ) {
                binding.treeStatusValue.selectedItem.toString()
            } else null,
            fileIds = emptyList()
        )
    }

    /**
     * Собирает данные из всех полей ввода.
     * @return Объект класса [TreeDetailUIModel] с заполненными данными
     * @throws IllegalStateException функция вызвана вне состояния DataLoaded
     * @see [checkInputFields]
     * */
    private suspend fun getTreeDetail(): TreeDetailUIModel {
        val state = viewModel.currentState
        if (state !is EditTreeContract.EditTreeViewState.DataLoaded) {
            throw IllegalStateException("")
        }
        return TreeDetailUIModel(
            id = state.treeData.id,
            coord = state.treeData.coord,
            species = viewModel.getSpeciesByName(binding.treeSpeciesValue.selectedItem.toString())!!,
            height = if (!binding.heightOfTheFirstBranchValue.text.isNullOrBlank())
                binding.heightOfTheFirstBranchValue.text.toString().toDouble()
            else null,
            numberOfTrunks = if (!binding.numberOfTrunksValue.text.isNullOrBlank())
                binding.numberOfTrunksValue.text.toString().toInt()
            else null,
            trunkGirth = if (!binding.trunkGirthValue.text.isNullOrBlank())
                binding.trunkGirthValue.text.toString().toDouble()
            else null,
            diameterOfCrown = if (!binding.diameterOfCrownValue.text.isNullOrBlank())
                binding.diameterOfCrownValue.text.toString().toDouble()
            else throw IllegalArgumentException("Поле ${binding.diameterOfCrownValue::class.simpleName} не должно быть пустым!"),
            heightOfTheFirstBranch = if (!binding.heightOfTheFirstBranchValue.text.isNullOrBlank())
                binding.heightOfTheFirstBranchValue.text.toString().toDouble()
            else null,
            conditionAssessment = binding.conditionAssessmentValue.progress,
            age = if (!binding.ageValue.text.isNullOrBlank())
                binding.ageValue.text.toString().toInt()
            else null,
            treePlantingType = if (binding.plantingTypeValue.selectedItem.toString()
                != resources.getStringArray(R.array.planting_types)[0]
            ) {
                binding.plantingTypeValue.selectedItem.toString()
            } else null,
            createTime = state.treeData.createTime,
            updateTime = System.currentTimeMillis().toString(),
            authorId = state.treeData.authorId,
            status = if (binding.treeStatusValue.selectedItem.toString()
                != resources.getStringArray(R.array.status_types)[0]
            ) {
                binding.treeStatusValue.selectedItem.toString()
            } else null,
            fileIds = emptyList()
        )
    }

    private fun formatTextTime(textTime: String): String {
        val date = if ('.' in textTime) {
            Date(textTime.toDouble().toLong() * 1000)
        } else Date(textTime.toLong())
        return SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(date)
    }

    /**
     * Проверка корректности введённых числовых данных (в полях, где должны быть целые числа).
     * @return true, если поля корректны, иначе false.
     * */
    private fun checkInputFields(): Boolean {
        var result = true
        if (binding.treeSpeciesValue.selectedItem.toString() == getString(R.string.select_tree_species)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.select_species),
                Toast.LENGTH_SHORT
            ).show()
            result = false
        }
        if (binding.diameterOfCrownValue.text.isNullOrBlank()) {
            binding.diameterOfCrownValue.error = getString(R.string.empty_field_warning)
            result = false
        }
        if (!binding.numberOfTrunksValue.text.isNullOrBlank() &&
            !tryConvertInputTextValueToNumber(binding.numberOfTrunksValue)
        ) {
            result = false
        }
        if (!binding.ageValue.text.isNullOrBlank() &&
            !tryConvertInputTextValueToNumber(binding.ageValue)
        ) {
            result = false
        }
        return result
    }

    /**
     * Пытается привети значение в inputEditText к целому числу. В случае провала выставляет ошибку в поле.
     * */
    private fun tryConvertInputTextValueToNumber(inputEditText: TextInputEditText): Boolean {
        return try {
            inputEditText.text.toString().toInt()
            true
        } catch (e: NumberFormatException) {
            inputEditText.error = "Введённое значение должно быть целым числом!"
            false
        }
    }

    /**
     * Заполняет только спинеры и выставляет заглушки.
     * */
    private fun showNewTreeDetailData(newTreeDetail: NewTreeDetailUIModel) {
        setupTreeLocation(newTreeDetail.coord)
        setupSpinners(newTreeDetail.species?.name)

        showTextInEditText(binding.numberOfTrunksValue, newTreeDetail.numberOfTrunks.toString())
        showTextInEditText(binding.trunkGirthValue, newTreeDetail.trunkGirth.toString())
        showTextInEditText(binding.diameterOfCrownValue, newTreeDetail.diameterOfCrown.toString())
        showTextInEditText(binding.ageValue, newTreeDetail.age.toString())
        showTextInEditText(
            binding.heightOfTheFirstBranchValue,
            newTreeDetail.heightOfTheFirstBranch.toString()
        )
        if (binding.conditionAssessmentTextValue.text.toString().isEmpty()) {
            binding.conditionAssessmentValue.progress = newTreeDetail.conditionAssessment ?: 0
        }
        binding.conditionAssessmentTextValue.text = getString(
            R.string.condition_assessment_holder,
            binding.conditionAssessmentValue.progress.toString()
        )

        binding.treeIdValue.text = getString(R.string.tree_id_plug)
        binding.createTimeValue.text = formatTextTime(newTreeDetail.createTime)
        binding.updateTimeValue.text = formatTextTime(newTreeDetail.updateTime)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { editTreeViewState ->
                        cleanUI()
                        when (editTreeViewState) {
                            is EditTreeContract.EditTreeViewState.Idle -> {
                            }
                            is EditTreeContract.EditTreeViewState.NewTreeData -> {
                                binding.topAppBar.setTitle(R.string.new_tree)
                                onNewTreeDataState(treeDetail = editTreeViewState.treeDetail)
                                photoAdapter.submitList(editTreeViewState.photoList)
                            }
                            is EditTreeContract.EditTreeViewState.DataLoading -> {
                                onDataLoadingState()
                            }
                            is EditTreeContract.EditTreeViewState.DataLoaded -> {
                                binding.topAppBar.title = editTreeViewState.treeData.species.name
                                onDataLoadedState(treeDetail = editTreeViewState.treeData)
                                photoAdapter.submitList(editTreeViewState.photoList)
                            }
                            is EditTreeContract.EditTreeViewState.Error -> {
                                onErrorState()
                            }
                        }
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is EditTreeContract.TreeDetailEffect.ShowErrorMessage -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.error_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is EditTreeContract.TreeDetailEffect.BackOnBackStack -> {
                                val navController = findNavController()
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cleanUI() {
        binding.loadingContent.visibility = View.GONE
        binding.mainContent.visibility = View.GONE
        binding.saveDataButton.visibility = View.GONE
        binding.gradient.visibility = View.GONE
        binding.errorContent.visibility = View.GONE
    }

    private fun onNewTreeDataState(treeDetail: NewTreeDetailUIModel) {
        binding.mainContent.visibility = View.VISIBLE
        binding.saveDataButton.visibility = View.VISIBLE
        binding.gradient.visibility = View.VISIBLE
        showNewTreeDetailData(newTreeDetail = treeDetail)
    }

    private fun onDataLoadingState() {
        binding.loadingContent.visibility = View.VISIBLE
    }

    private fun onDataLoadedState(treeDetail: TreeDetailUIModel) {
        binding.mainContent.visibility = View.VISIBLE
        binding.saveDataButton.visibility = View.VISIBLE
        binding.gradient.visibility = View.VISIBLE
        showTreeDetailData(treeDetail = treeDetail)
    }

    private fun onErrorState() {
        binding.errorContent.visibility = View.VISIBLE
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        val imagePaths = uris.map { it.toString() }
        viewModel.setEvent(EditTreeContract.EditTreeEvent.OnImagesSelected(imagePaths))
    }
}