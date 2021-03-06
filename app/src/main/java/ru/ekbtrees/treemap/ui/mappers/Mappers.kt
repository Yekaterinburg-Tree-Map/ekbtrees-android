package ru.ekbtrees.treemap.ui.mappers

import com.google.android.gms.maps.model.LatLng
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.ui.model.NewTreeDetailUIModel
import ru.ekbtrees.treemap.ui.model.RegionBoundsUIModel
import ru.ekbtrees.treemap.ui.model.SpeciesUIModel
import ru.ekbtrees.treemap.ui.model.TreeDetailUIModel

fun LatLonEntity.toLatLng(): LatLng {
    return LatLng(lat, lon)
}

fun RegionBoundsUIModel.toRegionBoundsUIModel(): RegionBoundsEntity {
    val topLeft = LatLonEntity(topLeft.latitude, topLeft.longitude)
    val bottomRight = LatLonEntity(bottomRight.latitude, bottomRight.longitude)
    return RegionBoundsEntity(topLeft, bottomRight)
}

fun TreeDetailUIModel.toTreeDetailEntity(): TreeDetailEntity =
    TreeDetailEntity(
        id = id,
        coord = LatLonEntity(coord.latitude, coord.longitude),
        species = SpeciesEntity(species.id, species.color, species.name),
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
        fileIds = fileIds
    )

fun NewTreeDetailUIModel.toNewTreeDetailEntity(): NewTreeDetailEntity {
    return NewTreeDetailEntity(
        coord = LatLonEntity(coord.latitude, coord.longitude),
        species = SpeciesEntity(species!!.id, species.color, species.name),
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
        fileIds = fileIds
    )
}

fun TreeDetailEntity.toTreeDetailUIModel(): TreeDetailUIModel =
    TreeDetailUIModel(
        id = id,
        coord = coord.toLatLng(),
        species = SpeciesUIModel(species.id, species.color, species.name),
        height = height,
        numberOfTrunks = numberOfTrunks,
        trunkGirth = trunkGirth,
        diameterOfCrown = diameterOfCrown,
        heightOfTheFirstBranch = heightOfTheFirstBranch,
        conditionAssessment = conditionAssessment,
        age = age,
        treePlantingType = treePlantingType,
        createTime = createTime,
        updateTime = createTime,
        authorId = authorId,
        status = status,
        fileIds = fileIds as List<Int>
    )

fun SpeciesEntity.toSpeciesUIModel(): SpeciesUIModel = SpeciesUIModel(id, color, name)