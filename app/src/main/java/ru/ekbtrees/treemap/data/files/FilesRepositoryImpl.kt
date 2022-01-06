package ru.ekbtrees.treemap.data.files

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.ekbtrees.treemap.data.files.api.UploadFileApiService
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto
import ru.ekbtrees.treemap.domain.repositories.FilesRepository
import java.io.ByteArrayOutputStream

class FilesRepositoryImpl(
    private val context: Application,
    private val coroutineScope: CoroutineScope,
    private val apiService: UploadFileApiService
) : FilesRepository {

    override suspend fun upload(filePath: String): Flow<UploadFileDto> {
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
        val result = apiService.sendFile(body = body)
        val delegate = UploadFileRequestObserverDelegate(coroutineScope)
        return delegate.flow
    }
}