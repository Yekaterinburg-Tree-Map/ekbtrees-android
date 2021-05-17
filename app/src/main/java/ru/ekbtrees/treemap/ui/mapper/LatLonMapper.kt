package ru.ekbtrees.treemap.ui.mapper

import com.google.android.gms.maps.model.LatLng
import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import ru.ekbtrees.treemap.domain.mapper.Mapper

class LatLonMapper: Mapper<LatLonEntity, LatLng> {
    override fun map(from: LatLonEntity): LatLng {
        return LatLng(from.lat, from.lon)
    }
}