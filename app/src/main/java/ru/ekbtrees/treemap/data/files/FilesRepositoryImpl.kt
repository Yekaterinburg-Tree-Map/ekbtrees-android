package ru.ekbtrees.treemap.data.files

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import ru.ekbtrees.treemap.BuildConfig
import ru.ekbtrees.treemap.constants.NetworkConstants.BASE_URL
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto
import ru.ekbtrees.treemap.domain.repositories.FilesRepository

class FilesRepositoryImpl(
    private val context: Application,
    private val lifecycleOwner: LifecycleOwner,
    private val coroutineScope: CoroutineScope
) : FilesRepository {

    override suspend fun upload(filePath: String): Flow<UploadFileDto> {
        val delegate = UploadFileRequestObserverDelegate(coroutineScope)
        MultipartUploadRequest(context, "$BASE_URL/file/upload")
            .setBearerAuth(BuildConfig.access_token)
            .addFileToUpload(filePath = filePath, parameterName = "file", contentType = "*/*")
            .subscribe(context, lifecycleOwner, delegate)
        return delegate.flow
    }
}