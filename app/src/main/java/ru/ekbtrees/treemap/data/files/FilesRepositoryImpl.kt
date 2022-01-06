package ru.ekbtrees.treemap.data.files

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.ekbtrees.treemap.data.files.api.FilesApiService
import ru.ekbtrees.treemap.data.result.RetrofitResult
import ru.ekbtrees.treemap.domain.repositories.FilesRepository
import ru.ekbtrees.treemap.domain.utils.Resource
import java.io.ByteArrayOutputStream

class FilesRepositoryImpl(
    private val context: Application,
    private val apiService: FilesApiService
) : FilesRepository {

    override suspend fun upload(filePath: String): Resource<Long> {
        val inputStream = context.contentResolver.openInputStream(Uri.parse(filePath))
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            name = "file",
            filename = "file",
            body = byteArray.toRequestBody(
                contentType = "*/*".toMediaType(),
                offset = 0,
                byteCount = byteArray.size
            )
        )
        return when (val result = apiService.sendFile(body = body)) {
            is RetrofitResult.Success -> {
                Resource.Success(result.value.toLong())
            }
            is RetrofitResult.Failure<*> -> Resource.Error()
        }
    }
}