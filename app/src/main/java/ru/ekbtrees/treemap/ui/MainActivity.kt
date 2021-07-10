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
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.map.TreeMapFragment

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

    private fun onTreeSelected(treeId: String) {
        // Выводим фрагмент описания дерева по его id.
    }

    private fun addNewTree(location: LatLng) {
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