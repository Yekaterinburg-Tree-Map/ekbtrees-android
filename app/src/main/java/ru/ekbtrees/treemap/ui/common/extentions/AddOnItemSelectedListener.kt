package ru.ekbtrees.treemap.ui.common.extentions

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

fun AppCompatSpinner.addOnItemSelectedListener(onItemSelected: (String) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parentView: AdapterView<*>?,
            selectedItemView: View?,
            position: Int,
            id: Long
        ) {
            onItemSelected(selectedItem.toString())
        }

        override fun onNothingSelected(parentView: AdapterView<*>?) {
        }
    }
}