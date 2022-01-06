package ru.ekbtrees.treemap.data.files

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto
import ru.ekbtrees.treemap.domain.repositories.FilesRepository

class FilesRepositoryImpl(
    private val context: Application,
    private val coroutineScope: CoroutineScope
) : FilesRepository {

    override suspend fun upload(filePath: String): Flow<UploadFileDto> {
        val delegate = UploadFileRequestObserverDelegate(coroutineScope)
        return delegate.flow
    }
}