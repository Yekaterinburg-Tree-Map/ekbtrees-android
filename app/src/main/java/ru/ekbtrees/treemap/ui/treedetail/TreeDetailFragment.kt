package ru.ekbtrees.treemap.ui.treedetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.Klaxon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.databinding.FragmentTreeDetailBinding
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.mvi.contract.TreeDetailContract
import java.util.*

private const val TAG = "TreeDetailFragment"
private const val ARG_PARAM1 = "TreeId"

/**
 * Фрагмент детализауии дерева.
 * */
@AndroidEntryPoint
class TreeDetailFragment : Fragment() {
    private lateinit var stringifiedTree: String
    private lateinit var parsedTree: TreeEntity

    private val treeDetailViewModel: TreeDetailViewModel by viewModels()

    private lateinit var binding: FragmentTreeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stringifiedTree = it.getString(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTreeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        observeViewModel()
        super.onResume()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            treeDetailViewModel.uiState.collect { viewState ->
                when (viewState) {
                    is TreeDetailContract.TreeDetailViewState.Idle -> {
                        parsedTree = Klaxon().parse<TreeEntity>(stringifiedTree)!!
                        binding.latitudeValue.text = parsedTree.coord.lat.toString()
                        binding.longitudeValue.text = parsedTree.coord.lon.toString()
                        binding.treeSpeciesValue.text = parsedTree.species.name
                        binding.diameterValue.text = parsedTree.diameter.toString()
                    }
                    is TreeDetailContract.TreeDetailViewState.TreeDetailLoadingState -> {
                        // Show progress bar
                    }
                    is TreeDetailContract.TreeDetailViewState.TreeDetailLoadedState -> {
                        // Show tree detail data
                    }
                    is TreeDetailContract.TreeDetailViewState.TreeDetailErrorState -> {
                        // Show error
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(treeId: String) =
            TreeDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, treeId)
                }
            }
    }
}