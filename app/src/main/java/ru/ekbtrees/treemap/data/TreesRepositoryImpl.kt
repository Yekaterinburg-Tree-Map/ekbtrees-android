package ru.ekbtrees.treemap.data

import android.graphics.Bitmap
import android.graphics.Color
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.data.dto.ClusterTreesDto
import ru.ekbtrees.treemap.data.mappers.*
import ru.ekbtrees.treemap.data.result.RetrofitResult
import ru.ekbtrees.treemap.data.result.asSuccess
import ru.ekbtrees.treemap.data.result.isSuccess
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import ru.ekbtrees.treemap.domain.utils.UploadResult

class TreesRepositoryImpl(
    private val treesApiService: TreesApiService
) : TreesRepository {

    private var colorList: List<Int>
    private var species: List<SpeciesEntity>? = null

    init {
        colorList = generateColors()
    }

    override suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity> {
        val result = treesApiService.getClusterTreesInRegion(
            regionBoundsEntity.topLeft.lat,
            regionBoundsEntity.topLeft.lon,
            regionBoundsEntity.bottomRight.lat,
            regionBoundsEntity.bottomRight.lon
        )
        when {
            result.isSuccess() -> {
                val clusterList: List<ClusterTreesDto> = result.asSuccess().value
                if (clusterList.isEmpty()) return emptyList()
                val clusterTreesEntityList = mutableListOf<ClusterTreesEntity>()
                clusterList.forEach { clusterTreesDto ->
                    clusterTreesEntityList.add(clusterTreesDto.toClusterTreeEntity())
                }
                return clusterTreesEntityList
            }
            else -> {
                error("Exception while HTTP request")
            }
        }
    }

    override suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity)
            : Collection<TreeEntity> {

        val result = treesApiService.getTreesInRegion(
            regionBoundsEntity.topLeft.lat,
            regionBoundsEntity.topLeft.lon,
            regionBoundsEntity.bottomRight.lat,
            regionBoundsEntity.bottomRight.lon
        )
        return when (result) {
            is RetrofitResult.Success -> {
                if (result.value.isEmpty()) {
                    emptyList()
                } else {
                    result.value.map { mapTreeDto ->
                        mapTreeDto.toTreeEntity(getSpeciesBy(name = mapTreeDto.species.name))
                    }
                }
            }
            else -> error("Exception while HTTP request")
        }
    }

    override suspend fun getSpecies(): Collection<SpeciesEntity> {
        if (species == null) {
            val speciesDtoList = treesApiService.getAllSpecies()
            if (speciesDtoList.isEmpty()) return emptyList()
            val speciesEntityList = mutableListOf<SpeciesEntity>()
            speciesDtoList.forEachIndexed { index, speciesDto ->
                val speciesEntity =
                    SpeciesEntity(speciesDto.id.toString(), colorList[index], speciesDto.name)
                speciesEntityList.add(speciesEntity)
            }
            species = speciesEntityList
            return species as Collection<SpeciesEntity>
        }
        return species as Collection<SpeciesEntity>
    }

    override suspend fun getTreeDetailBy(id: String): TreeDetailEntity {
        when (val result = treesApiService.getTreeDetailBy(treeId = id.toInt())) {
            is RetrofitResult.Success -> {
                return result.value.toTreeDetailEntity()
            }
            is RetrofitResult.Failure<*> -> {
                error(result)
            }
            else -> error("Unexpected case")
        }
    }

    override suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): UploadResult {
        val treeDetailDto = treeDetail.toTreeDetailDto()
        return when (treesApiService.saveTreeDetail(treeDetailDto)) {
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }

    override suspend fun uploadNewTreeDetail(treeDetail: NewTreeDetailEntity): UploadResult {
        val newTreeDetail = treeDetail.toNewTreeDetailDto()
        return when (treesApiService.createNewTreeDetail(newTreeDetail)) {
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }

    private suspend fun getSpeciesBy(name: String): SpeciesEntity {
        for (species in getSpecies()) {
            if (species.name.lowercase() == name.lowercase()) {
                return species
            }
        }
        throw IllegalArgumentException("Порода $name не была найдена.")
    }

    private fun generateColors(): List<Int> {
        val colors = mutableListOf<Int>()
        var greenColor = 7
        while (greenColor < 256) {
            val hexColor = Integer.toHexString(greenColor).uppercase()
            val color =
                if (hexColor.length > 1) {
                    hexColor
                } else {
                    "0$hexColor"
                }
            colors.add(Color.parseColor("#00${color}00"))
            greenColor += 8
        }
        var blueColor = 7
        while (blueColor < 256) {
            val hexColor = Integer.toHexString(blueColor).uppercase()
            val color =
                if (hexColor.length > 1) {
                    hexColor
                } else {
                    "0$hexColor"
                }
            colors.add(Color.parseColor("#00${color}00"))
            blueColor += 8
        }
        return colors
    }

    override suspend fun sendFile(image: Bitmap): UploadResult {
        error("Deprecated")
    }
}