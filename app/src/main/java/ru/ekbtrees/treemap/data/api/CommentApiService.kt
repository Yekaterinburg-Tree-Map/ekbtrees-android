package ru.ekbtrees.treemap.data.api

import retrofit2.http.*
import ru.ekbtrees.treemap.data.dto.NewTreeCommentDto
import ru.ekbtrees.treemap.data.dto.TreeCommentDto
import ru.ekbtrees.treemap.data.result.RetrofitResult

interface CommentApiService {
    @GET("comment/by-tree/{treeId}")
    suspend fun getTreeCommentBy(
        @Path("treeId") id: Int
    ): RetrofitResult<List<TreeCommentDto>>

    @POST("comment")
    suspend fun saveTreeComment(
        @Body treeCommentDto: NewTreeCommentDto
    ): RetrofitResult<Unit>

    @PUT("comment/{id}")
    suspend fun updateTreeComment(
        @Path("id") id: Int
    ): RetrofitResult<Unit>

    @DELETE("comment/{id}")
    suspend fun deleteTreeComment(
        @Path("id") id: Int
    ): RetrofitResult<Unit>
}