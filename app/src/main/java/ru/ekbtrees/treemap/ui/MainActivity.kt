package ru.ekbtrees.treemap.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.map.TreeMapFragment
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
            is TreeDetailFragment -> {
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    title = getString(R.string.tree_detail)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.treeSelected.collect {
                onTreeSelected()
            }
        }
        lifecycleScope.launchWhenStarted {
            sharedViewModel.addNewTree.collect {
                addNewTree()
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

    private fun onTreeSelected() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.tree_detail)
        }
    }

    private fun addNewTree() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.new_tree)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        lifecycleScope.launch {
            if (grantResults.isNotEmpty()) {
                sharedViewModel.sendPermissionResult(
                    TreeMapFragment.LOCATION_REQUEST_CODE,
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}