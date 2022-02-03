package ru.ekbtrees.treemap.data.file

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.ekbtrees.treemap.data.file.api.FileApiService
import ru.ekbtrees.treemap.data.result.RetrofitResult
import ru.ekbtrees.treemap.domain.repositories.FileRepository
import ru.ekbtrees.treemap.domain.utils.Resource
import java.io.ByteArrayOutputStream

private const val TAG = "FileRepositoryImpl"

class FileRepositoryImpl(
    private val context: Application,
    private val apiService: FileApiService
) : FileRepository {

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
        Log.d(TAG, "Start file upload 0% of ${byteArray.size} bytes")
        return when (val result = apiService.sendFile(body = body)) {
            is RetrofitResult.Success -> {
                Log.d(TAG, "Successful uploaded 100% of ${byteArray.size} bytes")
                Log.d(TAG, "Uploaded file id ${result.value.toLong()}")
                Resource.Success(result.value.toLong())
            }
            is RetrofitResult.Failure<*> -> {
                Log.d(TAG, "Failed to upload file $filePath\n${result.error?.printStackTrace()}")
                Resource.Error()
            }
        }
    }

    override suspend fun deleteFile(fileId: Long): Resource<Unit> =
        when (apiService.deleteFile(fileId = fileId)) {
            is RetrofitResult.Success -> {
                Log.d(TAG, "Successful deleted file $fileId")
                Resource.Success(Unit)
            }
            is RetrofitResult.Failure<*> -> {
                Log.d(TAG, "Failed to delete file $fileId")
                Resource.Error()
            }
        }
}