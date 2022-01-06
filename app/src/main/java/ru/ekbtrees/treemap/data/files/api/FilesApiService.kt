package ru.ekbtrees.treemap.data.files.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.ekbtrees.treemap.data.result.RetrofitResult

interface FilesApiService {

    @POST("file/upload")
    @Multipart
    fun sendFile(@Part body: MultipartBody.Part): RetrofitResult<String>
}