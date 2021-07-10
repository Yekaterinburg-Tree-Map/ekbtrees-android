package ru.ekbtrees.treemap.data.mappers

import android.graphics.Color
import ru.ekbtrees.treemap.data.dto.ClusterTreesDto
import ru.ekbtrees.treemap.data.dto.MapTreeDto
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.mapper.Mapper

class TreeDtoMapper() : Mapper<MapTreeDto, TreeEntity> {
    override fun map(from: MapTreeDto): TreeEntity {
        val speciesEntity = SpeciesEntity(
            from.species.id.toString(),
            Color.parseColor("#000000"),
            from.species.name
        )
        val latLonEntity = LatLonEntity(from.coord.lat, from.coord.lon)
        return TreeEntity(from.id.toString(), from.diameter, speciesEntity, latLonEntity)
    }
}

class ClusterTreeDtoMapper(): Mapper<ClusterTreesDto, ClusterTreesEntity> {
    override fun map(from: ClusterTreesDto): ClusterTreesEntity {
        val latLonEntity = LatLonEntity(from.coord.lat, from.coord.lon)
        return ClusterTreesEntity(from.count, latLonEntity)
    }
}