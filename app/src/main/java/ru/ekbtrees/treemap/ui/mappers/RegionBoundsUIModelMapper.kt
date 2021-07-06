package ru.ekbtrees.treemap.ui.mappers

import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import ru.ekbtrees.treemap.domain.entity.RegionBoundsEntity
import ru.ekbtrees.treemap.domain.mapper.Mapper
import ru.ekbtrees.treemap.ui.model.RegionBoundsUIModel

class RegionBoundsUIModelMapper:Mapper<RegionBoundsUIModel,RegionBoundsEntity> {
    override fun map(from: RegionBoundsUIModel): RegionBoundsEntity {
        val topLeft = LatLonEntity(from.topLeft.latitude, from.topLeft.longitude)
        val bottomRight = LatLonEntity(from.bottomRight.latitude, from.bottomRight.longitude)
        return RegionBoundsEntity(topLeft, bottomRight)
    }
}