package ru.ekbtrees.treemap.data

import android.content.Context
import android.graphics.Color
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import java.io.IOException
import java.lang.Exception
import java.nio.charset.Charset

class TreesRepositoryImpl(private val context: Context) : TreesRepository {

    override fun getAllClusteringTrees(): Collection<ClusterTreesEntity> {
        TODO("Not yet implemented")
    }

    override fun getTreesInClusteringBy(): Collection<TreeEntity> {
        // asset location: app/src/main/assets
        var id = 0
        val json = loadJSON(context)
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
            return JSONObject("""
                {
                    "features": []
                }
            """.trimIndent())
        }
        return JSONObject(jsonString)
    }

    override fun getTreeBy(id: String): TreeEntity {
        TODO("Not yet implemented")
    }

    override fun getSpecies(): Collection<SpeciesEntity> {
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

    private fun getSpeciesByName(name: String): SpeciesEntity {
        for (type in getSpecies()) {
            if (type.name == name) return type
        }
        throw Exception("$name не был определён")
    }

}