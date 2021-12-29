package ru.ekbtrees.treemap.data.api

import retrofit2.http.*
import ru.ekbtrees.treemap.data.dto.NewTreeCommentDto
import ru.ekbtrees.treemap.data.dto.TreeCommentDto
import ru.ekbtrees.treemap.data.result.RetrofitResult

interface CommentApiService {
    @GET("tree/comment/get/{id}")
    suspend fun getTreeCommentBy(
        @Path("id") treeId: Int
    ): RetrofitResult<TreeCommentDto>

    @POST("tree/comment/save")
    suspend fun saveTreeComment(
        @Body treeCommentDto: NewTreeCommentDto
    ): RetrofitResult<Unit>

    @PUT("tree/comment/update")
    suspend fun updateTreeComment(
        @Body treeCommentDto: TreeCommentDto
    ): RetrofitResult<Unit>

    @DELETE("tree/comment/delete")
    suspend fun deleteTreeComment(): RetrofitResult<Unit>
}