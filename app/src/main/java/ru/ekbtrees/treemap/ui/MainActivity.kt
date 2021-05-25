package ru.ekbtrees.treemap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collect
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.map.TreeMapFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = TreeMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment).commit()
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.treeSelected.collect { treeId ->
                onTreeSelected(treeId)
            }
        }
        lifecycleScope.launchWhenStarted {
            sharedViewModel.addNewTree.collect { location ->
                addNewTree(location)
            }
        }
    }

    private fun onTreeSelected(treeId: String) {
        // Выводим фрагмент описания дерева по его id.
    }

    private fun addNewTree(location: LatLng) {
        val fragment = EditTreeFragment.newInstance(location)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}