package ru.ekbtrees.treemap.data.mappers

import android.graphics.Color
import ru.ekbtrees.treemap.data.dto.*
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.mapper.Mapper

class TreeDtoMapper(private val speciesEntity: SpeciesEntity) : Mapper<MapTreeDto, TreeEntity> {
    override fun map(from: MapTreeDto): TreeEntity {
        val latLonEntity = LatLonDtoMapper().map(from.coord)
        return TreeEntity(from.id.toString(), from.diameter, speciesEntity, latLonEntity)
    }
}

class ClusterTreeDtoMapper : Mapper<ClusterTreesDto, ClusterTreesEntity> {
    override fun map(from: ClusterTreesDto): ClusterTreesEntity {
        val latLonEntity = LatLonDtoMapper().map(from.coord)
        return ClusterTreesEntity(from.count, latLonEntity)
    }
}

class LatLonDtoMapper : Mapper<LatLonDto, LatLonEntity> {
    override fun map(from: LatLonDto): LatLonEntity {
        return LatLonEntity(lat = from.lat, lon = from.lon)
    }
}

class TreeDetailDtoMapper : Mapper<TreeDetailDto, TreeDetailEntity> {
    override fun map(from: TreeDetailDto): TreeDetailEntity {
        return TreeDetailEntity(
            id = from.id.toString(),
            coord = LatLonDtoMapper().map(from.coord),
            species = SpeciesEntity(
                from.species.id.toString(),
                Color.parseColor("#00FF00"),
                from.species.name
            ),
            height = from.height ?: 0.0,
            numberOfTrunks = from.numberOfTrunks ?: 0,
            trunkGirth = from.trunkGirth ?: 0.0,
            diameterOfCrown = from.diameterOfCrown ?: 0.0,
            heightOfTheFirstBranch = from.heightOfTheFirstBranch ?: 0.0,
            conditionAssessment = from.conditionAssessment ?: 0,
            age = from.age ?: 0,
            treePlantingType = from.treePlantingType ?: "",
            createTime = from.createTime,
            updateTime = from.updateTime ?: "",
            authorId = from.authorId ?: 0,
            status = from.status ?: "",
            fileIds = from.fileIds ?: emptyList()
        )
    }
}

class TreeDetailEntityMapper : Mapper<TreeDetailEntity, TreeDetailDto> {
    override fun map(from: TreeDetailEntity): TreeDetailDto {
        val latLon = LatLonDto(from.coord.lat, from.coord.lon)
        val species = SpeciesDto(from.species.id.toInt(), from.species.name)
        return TreeDetailDto(
            id = from.id.toInt(),
            coord = latLon,
            species = species,
            height = from.height,
            numberOfTrunks = from.numberOfTrunks,
            trunkGirth = from.trunkGirth,
            diameterOfCrown = from.diameterOfCrown,
            heightOfTheFirstBranch = from.heightOfTheFirstBranch,
            conditionAssessment = from.conditionAssessment,
            age = from.age,
            treePlantingType = from.treePlantingType,
            createTime = from.createTime,
            updateTime = from.updateTime,
            authorId = from.authorId,
            status = from.status,
            fileIds = from.fileIds as List<Int>
        )
    }
}