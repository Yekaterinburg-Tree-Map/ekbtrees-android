package ru.ekbtrees.treemap.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity
import ru.ekbtrees.treemap.domain.entity.RegionBoundsEntity

interface TreesApiService {

    @GET("api/trees-cluster/get-in-region?x1={x1}&y1={y1}&x2={x2}&y2={y2}")
    suspend fun getClusterTreesIn(
        @Query("x1") topLeftX: Double,
        @Query("y1") topLeftY: Double,
        @Query("x2") BotRightX: Double,
        @Query("y2") BotRightY: Double
    ): Call<Collection<ClusterTreesEntity>>

    @GET("api/tree-map-info/get-in-region?x1={x1}&y1={y1}&x2={x2}&y2={y2}")
    suspend fun getTreesIn(
        @Query("x1") topLeftX: Double,
        @Query("y1") topLeftY: Double,
        @Query("x2") BotRightX: Double,
        @Query("y2") BotRightY: Double
    ): Call<Collection<ClusterTreesEntity>>
}