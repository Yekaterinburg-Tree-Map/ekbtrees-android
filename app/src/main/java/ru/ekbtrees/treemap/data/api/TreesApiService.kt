package ru.ekbtrees.treemap.data.api

import retrofit2.http.*
import ru.ekbtrees.treemap.data.dto.*
import ru.ekbtrees.treemap.data.result.RetrofitResult

interface TreesApiService {

    @GET("trees-cluster/get-in-region")
    suspend fun getClusterTreesInRegion(
        @Query("x1") topLeftX: Double,
        @Query("y1") topLeftY: Double,
        @Query("x2") BotRightX: Double,
        @Query("y2") BotRightY: Double
    ): RetrofitResult<List<ClusterTreesDto>>

    @GET("tree-map-info/get-in-region")
    suspend fun getTreesInRegion(
        @Query("x1") topLeftX: Double,
        @Query("y1") topLeftY: Double,
        @Query("x2") BotRightX: Double,
        @Query("y2") BotRightY: Double
    ): RetrofitResult<List<MapTreeDto>>

    @GET("species/get-all")
    suspend fun getAllSpecies(): List<SpeciesDto>

    @GET("tree/get/{id}")
    suspend fun getTreeDetailBy(
        @Path("id") treeId: Int
    ): RetrofitResult<TreeDetailDto>

    @PUT("tree/save/{id}")
    suspend fun saveTreeDetail(
        @Path("id") treeId: Int,
        @Body treeDetailDto: TreeDetailDto
    ): RetrofitResult<Unit>

    @POST("tree")
    suspend fun createNewTreeDetail(
        @Body treeDetailDto: NewTreeDetailDto
    ): RetrofitResult<Unit>
}