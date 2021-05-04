package ru.ekbtrees.treemap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.ekbtrees.treemap.R
import ru.ekbtrees.treemap.ui.map.TreeMapFragment

class MainActivity : AppCompatActivity() {

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
}