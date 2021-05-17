package ru.ekbtrees.treemap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.edittree.EditTreeFragment
import ru.ekbtrees.treemap.ui.map.TreeMapFragment

class MainActivity : AppCompatActivity(), TreeMapFragment.TreeMapCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = TreeMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment).commit()
        }
    }

    override fun onTreeSelected(treeId: String) {
        // Выводим фрагмент описания дерева по его id.
    }

    override fun addNewTree(coordinate: LatLng) {
        val fragment = EditTreeFragment.newInstance(coordinate)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}