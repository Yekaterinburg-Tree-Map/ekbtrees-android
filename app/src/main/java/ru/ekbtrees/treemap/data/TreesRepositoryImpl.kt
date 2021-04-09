package ru.ekbtrees.treemap.data

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import ru.ekbtrees.treemap.domain.entity.SpeciesEntity
import ru.ekbtrees.treemap.domain.entity.TreeEntity
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import java.io.IOException
import java.nio.charset.Charset

class TreesRepositoryImpl : TreesRepository {

    override fun getAllClusteringTrees(): Collection<ClusterTreesEntity> {
        TODO("Not yet implemented")
    }

    override fun getTreesInClusteringBy(): Collection<TreeEntity> {
        TODO("Not yet implemented")
    }

    override fun getTreeBy(id: String): TreeEntity {
        TODO("Not yet implemented")
    }

    fun loadTreesFromJSONAsset(context: Context): Collection<TreeEntity> {
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
                        position.getString(1).toFloat(),
                        position.getString(0).toFloat()
                    )
                val diameter = treeData.getString("diameter_crown").toFloat()
                val genus = treeData.getString("genus:ru")
                val species = SpeciesEntity("no_id", "#0000FF00", genus)
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
            val size = rawFile.available()
            val buffer = ByteArray(size)
            rawFile.read(buffer)
            rawFile.close()
            jsonString = String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(
                this.javaClass.name,
                "Failed to load local asset. Check asset location and the name of a file."
            )
            return JSONObject("")
        }
        return JSONObject(jsonString)
    }

}