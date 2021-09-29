package ru.ekbtrees.treemap.data.api

import retrofit2.http.*
import ru.ekbtrees.treemap.data.dto.*
import ru.ekbtrees.treemap.data.result.Result

interface TreesApiService {

    @GET("trees-cluster/get-in-region")
    suspend fun getClusterTreesInRegion(
        @Query("x1") topLeftX: Double,
        @Query("y1") topLeftY: Double,
        @Query("x2") BotRightX: Double,
        @Query("y2") BotRightY: Double
    ): Result<List<ClusterTreesDto>>

    @GET("tree-map-info/get-in-region")
    suspend fun getTreesInRegion(
        @Query("x1") topLeftX: Double,
        @Query("y1") topLeftY: Double,
        @Query("x2") BotRightX: Double,
        @Query("y2") BotRightY: Double
    ): Result<List<MapTreeDto>>

    @GET("species/get-all")
    suspend fun getAllSpecies(): List<SpeciesDto>

    @GET("tree/get/{id}")
    suspend fun getTreeDetailBy(
        @Path("id") treeId: Int
    ): Result<TreeDetailDto>

    @POST("tree/save")
    suspend fun saveTreeDetail(
        @Body treeDetailDto: TreeDetailDto
    ): Result<Unit>

    @POST("tree/save")
    suspend fun createNewTreeDetail(
        @Body treeDetailDto: NewTreeDetailDto
    ): Result<Unit>
}