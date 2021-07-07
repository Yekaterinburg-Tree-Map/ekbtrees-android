package ru.ekbtrees.treemap.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.LatLonEntity
import java.lang.reflect.Type

class ClustersDeserializer : JsonDeserializer<Collection<ClusterTreesEntity>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Collection<ClusterTreesEntity> {
        val result = mutableListOf<ClusterTreesEntity>()
        if (json == null) return emptyList()
        val jsonList = json.asJsonArray
        if (jsonList.size() == 0) return emptyList()
        for (jsonCluster in jsonList) {
            val jsonCenter = jsonCluster.asJsonObject.get("center").asJsonObject
            val latitude = jsonCenter.get("latitude").asDouble
            val longitude = jsonCenter.get("longitude").asDouble
            val latLonEntity = LatLonEntity(lat = latitude, lon = longitude)
            val count = jsonCluster.asJsonObject.get("count").asInt
            val clusterTreesEntity = ClusterTreesEntity(count = count, coord = latLonEntity)
            result.add(clusterTreesEntity)
        }
        return result
    }
}