package ru.ekbtrees.treemap.data.file.api

import okhttp3.MultipartBody
import retrofit2.http.*
import ru.ekbtrees.treemap.data.result.RetrofitResult

interface FileApiService {

    @POST("file/upload")
    @Multipart
    suspend fun sendFile(@Part body: MultipartBody.Part): RetrofitResult<String>
}