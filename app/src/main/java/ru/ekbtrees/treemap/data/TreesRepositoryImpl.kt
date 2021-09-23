package ru.ekbtrees.treemap.data

import android.content.Context
import android.graphics.Color
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.data.dto.ClusterTreesDto
import ru.ekbtrees.treemap.data.dto.MapTreeDto
import ru.ekbtrees.treemap.data.mappers.*
import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import java.io.IOException
import java.nio.charset.Charset
import kotlin.Exception

class TreesRepositoryImpl(
    private val context: Context,
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

    override fun getTrees(): Collection<TreeEntity> {
        // asset location: app/src/main/assets
        var id = 0
        val json = loadJSON(context = context)
        val result = mutableListOf<TreeEntity>()
        val arr = json.getJSONArray("features")
        for (i in 0 until arr.length()) {
            try {
                val treeData = arr.getJSONObject(i).getJSONObject("properties")
                val position =
                    arr.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates")
                val latLon =
                    LatLonEntity(
                        position.getDouble(1),
                        position.getDouble(0)
                    )
                val diameter = treeData.getString("diameter_crown").toFloat()
                val genus = treeData.getString("genus:ru")
                val species = getSpeciesByName(genus)
                val treeEntity = TreeEntity("$id", diameter, species, latLon)
                id++
                result.add(treeEntity)
            } catch (e: JSONException) {
                continue
            }
        }
        return result
    }

    private fun loadJSON(context: Context): JSONObject {
        val jsonString: String?
        try {
            val rawFile = context.assets.open("weiner_park.geojson")
            val buffer: ByteArray
            rawFile.use {
                val size = rawFile.available()
                buffer = ByteArray(size)
                rawFile.read(buffer)
            }
            jsonString = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            Log.e(
                this.javaClass.name,
                "Failed to load local asset. Check asset location and the name of a file.",
                e
            )
            return JSONObject(
                """
                {
                    "features": []
                }
            """.trimIndent()
            )
        }
        return JSONObject(jsonString)
    }

    override fun getAllSpecies(): Collection<SpeciesEntity> {
        return arrayListOf(
            SpeciesEntity("1", Color.parseColor("#C8BEEB5A"), "клен"),
            SpeciesEntity("2", Color.parseColor("#C800FFBF"), "тополь"),
            SpeciesEntity("3", Color.parseColor("#C8ffbf00"), "липа"),
            SpeciesEntity("4", Color.parseColor("#C800ffbf"), "лиственница"),
            SpeciesEntity("5", Color.parseColor("#C8ff8000"), "береза"),
            SpeciesEntity("6", Color.parseColor("#C8ff00ff"), "вяз"),
            SpeciesEntity("7", Color.parseColor("#C800bfff"), "ель"),
        )
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

    private fun getSpeciesByName(name: String): SpeciesEntity {
        for (type in getAllSpecies()) {
            if (type.name == name) return type
        }
        throw Exception("$name не был определён")
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