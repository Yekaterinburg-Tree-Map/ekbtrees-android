package ru.ekbtrees.treemap.data.mappers

import android.graphics.Color
import ru.ekbtrees.treemap.data.dto.*
import ru.ekbtrees.treemap.domain.entity.*

fun MapTreeDto.toTreeEntity(speciesEntity: SpeciesEntity): TreeEntity {
    val latLonEntity = coord.toLatLonEntity()
    return TreeEntity(id.toString(), diameter, speciesEntity, latLonEntity)
}

fun ClusterTreesDto.toClusterTreeEntity(): ClusterTreesEntity {
    val latLonEntity = coord.toLatLonEntity()
    return ClusterTreesEntity(count, latLonEntity)
}

fun LatLonDto.toLatLonEntity(): LatLonEntity {
    return LatLonEntity(lat = lat, lon = lon)
}

fun TreeCommentDto.toTreeCommentEntity(): TreeCommentEntity {
    return TreeCommentEntity(
        id = id,
        authorId = authorId ?: 0,
        text = text,
        createTime = createTime
    )
}

fun TreeDetailDto.toTreeDetailEntity(): TreeDetailEntity {
    return TreeDetailEntity(
        id = id.toString(),
        coord = coord.toLatLonEntity(),
        species = SpeciesEntity(
            species.id.toString(),
            Color.parseColor("#00FF00"),
            species.name
        ),
        height = height ?: 0.0,
        numberOfTrunks = numberOfTrunks ?: 0,
        trunkGirth = trunkGirth ?: 0.0,
        diameterOfCrown = diameterOfCrown ?: 0.0,
        heightOfTheFirstBranch = heightOfTheFirstBranch ?: 0.0,
        conditionAssessment = conditionAssessment ?: 0,
        age = age ?: 0,
        treePlantingType = treePlantingType ?: "",
        createTime = createTime,
        updateTime = updateTime ?: "",
        authorId = authorId ?: 0,
        status = status ?: "",
        fileIds = fileIds ?: emptyList()
    )
}

fun TreeDetailEntity.toTreeDetailDto(): TreeDetailDto {
    val latLon = LatLonDto(coord.lat, coord.lon)
    val species = SpeciesDto(species.id.toInt(), species.name)
    return TreeDetailDto(
        id = id.toInt(),
        coord = latLon,
        species = species,
        height = height,
        numberOfTrunks = numberOfTrunks,
        trunkGirth = trunkGirth,
        diameterOfCrown = diameterOfCrown,
        heightOfTheFirstBranch = heightOfTheFirstBranch,
        conditionAssessment = conditionAssessment,
        age = age,
        treePlantingType = treePlantingType,
        createTime = createTime,
        updateTime = updateTime,
        authorId = authorId,
        status = status,
        fileIds = fileIds as List<Int>
    )
}

fun NewTreeDetailEntity.toNewTreeDetailDto(): NewTreeDetailDto {
    val latLonDto = LatLonDto(coord.lat, coord.lon)
    val speciesDto = SpeciesDto(species.id.toInt(), species.name)
    return NewTreeDetailDto(
        coord = latLonDto,
        species = speciesDto,
        height = height,
        numberOfTrunks = numberOfTrunks,
        trunkGirth = trunkGirth,
        diameterOfCrown = diameterOfCrown,
        heightOfTheFirstBranch = heightOfTheFirstBranch,
        conditionAssessment = conditionAssessment,
        age = age,
        treePlantingType = treePlantingType,
        createTime = createTime,
        updateTime = updateTime,
        authorId = authorId,
        status = status,
        fileIds = fileIds as List<Int>
    )
}

fun TreeCommentEntity.toTreeCommentDto(): TreeCommentDto{
    return TreeCommentDto(
        id = id,
        authorId = authorId,
        text = text,
        createTime = createTime
    )
}

fun NewTreeCommentEntity.toNewTreeCommentDto(): NewTreeCommentDto {
    return NewTreeCommentDto(
        authorId = authorId,
        text = text,
        createTime = createTime
    )
}