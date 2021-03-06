package ru.ekbtrees.treemap.ui.edittree

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel

sealed class EditTreeInstanceValue: Parcelable {
    @Parcelize
    class TreeLocation(val treeLocation: LatLng): EditTreeInstanceValue()

    @Parcelize
    class TreeId(val treeId: String): EditTreeInstanceValue()

    @Parcelize
    class NewTreeLocation(val treeDetail: TreeDetailUIModel): EditTreeInstanceValue()
}
