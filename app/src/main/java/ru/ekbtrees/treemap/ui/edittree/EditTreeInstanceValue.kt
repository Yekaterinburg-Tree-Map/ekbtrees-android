package ru.ekbtrees.treemap.ui.edittree

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

sealed class EditTreeInstanceValue: Parcelable {
    @Parcelize
    class TreeLocation(val treeLocation: LatLng): EditTreeInstanceValue()

    @Parcelize
    class TreeId(val treeId: String): EditTreeInstanceValue()

    @Parcelize
    class NewTreeLocation(val treeId: String, val newLocation: LatLng): EditTreeInstanceValue()
}
