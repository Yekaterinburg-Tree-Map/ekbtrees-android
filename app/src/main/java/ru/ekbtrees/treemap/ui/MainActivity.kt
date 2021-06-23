package ru.ekbtrees.treemap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collect
import dagger.hilt.android.AndroidEntryPoint
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.edittree.EditTreeInstanceValue
import ru.ekbtrees.treemap.ui.map.TreeMapFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        when (currentFragment) {
            null -> {
                val fragment = TreeMapFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit()
            }
            is EditTreeFragment -> {
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    title = getString(R.string.new_tree)
                }
            }
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

    override fun onBackPressed() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = getString(R.string.app_name)
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onTreeSelected(treeId: String) {
        // Выводим фрагмент описания дерева по его id.
    }

    private fun addNewTree(location: LatLng) {
        val fragment = EditTreeFragment.newInstance(EditTreeInstanceValue.TreeLocation(location))
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.new_tree)
        }
    }
}