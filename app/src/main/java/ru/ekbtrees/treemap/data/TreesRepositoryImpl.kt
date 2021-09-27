package ru.ekbtrees.treemap.data

import android.graphics.Color
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.data.dto.ClusterTreesDto
import ru.ekbtrees.treemap.data.dto.MapTreeDto
import ru.ekbtrees.treemap.data.mappers.*
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import kotlin.Exception

class TreesRepositoryImpl(
    private val treesApiService: TreesApiService
) : TreesRepository {

    private var colorList: List<Int>
    private var species: List<SpeciesEntity>? = null

    init {
        colorList = generateColors()
    }

    override suspend fun getTreeClusters(regionBoundsEntity: RegionBoundsEntity): Collection<ClusterTreesEntity> {
        val clustersList: List<ClusterTreesDto> = treesApiService.getClusterTreesInRegion(
            regionBoundsEntity.topLeft.lat,
            regionBoundsEntity.topLeft.lon,
            regionBoundsEntity.bottomRight.lat,
            regionBoundsEntity.bottomRight.lon
        )
        if (clustersList.isEmpty()) return emptyList()
        val clusterTreesEntityList = mutableListOf<ClusterTreesEntity>()
        clustersList.forEach { clusterTreesDto ->
            clusterTreesEntityList.add(clusterTreesDto.toClusterTreeEntity())
        }
        return clusterTreesEntityList
    }

    override suspend fun getMapTreesInRegion(regionBoundsEntity: RegionBoundsEntity): Collection<TreeEntity> {
        val treesList: List<MapTreeDto> = treesApiService.getTreesInRegion(
            regionBoundsEntity.topLeft.lat,
            regionBoundsEntity.topLeft.lon,
            regionBoundsEntity.bottomRight.lat,
            regionBoundsEntity.bottomRight.lon
        )
        if (treesList.isEmpty()) {
            return emptyList()
        }
        return treesList.map { mapTreeDto ->
            mapTreeDto.toTreeEntity(getSpeciesBy(name = mapTreeDto.species.name))
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
        val treeDetailDto = treesApiService.getTreeDetailBy(treeId = id.toInt())
        return treeDetailDto.toTreeDetailEntity()
    }

    override suspend fun uploadTreeDetail(treeDetail: TreeDetailEntity): Result<Unit> {
        val treeDetailDto = treeDetail.toTreeDetailDto()
        return try {
            val response = treesApiService.saveTreeDetail(treeDetailDto)
            if (response.code() == 201) {
                Result.success(Unit)
            } else {
                Result.failure(Exception())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun uploadNewTreeDetail(treeDetail: NewTreeDetailEntity): Result<Unit> {
        val newTreeDetail = treeDetail.toNewTreeDetailDto()
        val response = treesApiService.createNewTreeDetail(newTreeDetail)
        return if (response.code() == 201) {
            Result.success(Unit)
        } else {
            Result.failure(Exception())
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
}