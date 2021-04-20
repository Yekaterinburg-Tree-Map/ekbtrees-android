package ru.ekbtrees.treemap.ui

import android.widget.Toast
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener

class TreeTapListener : MapObjectTapListener{
    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        val data = p0.userData
        if (data is TreeData) {
            var instance = data.tree
            Toast.makeText(data.context, "ID дерева:${instance.id} | ${instance.latitude} ${instance.longitude} \n" +
                    "Диаметр кроны: ${instance.diameterOfCrown} Вид: ${instance.type}", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

}