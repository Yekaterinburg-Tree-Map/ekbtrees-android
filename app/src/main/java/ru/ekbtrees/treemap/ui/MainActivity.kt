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
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.treedetail.TreeDetailFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when (supportFragmentManager.findFragmentById(R.id.fragment_container)) {
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

    private fun onTreeSelected(treeEntity: TreeEntity) {
        val fragment = TreeDetailFragment.newInstance(treeEntity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun addNewTree(location: LatLng) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.new_tree)
        }
    }
}